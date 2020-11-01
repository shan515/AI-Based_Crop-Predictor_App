from flask import Flask
from flask import request
from flask import jsonify
import joblib
import csv
import requests
import json
import pickle
from Models.crop_class import *
from Models.production_class import *
import numpy as np
app = Flask(__name__)

scale_val = 0.1

month_dict = {
    'JAN': 0,
    'FEB': 1,
    'MAR': 2,
    'APR': 3,
    'MAY': 4,
    'JUN': 5,
    'JUL': 6,
    'AUG': 7,
    'SEP': 8,
    'OCT': 9,
    'NOV': 10,
    'DEC': 11
}


def get_avg(temps, predict_month):
    temp_arr = []
    idx_num = month_dict[predict_month]
    temp_arr.append(float(temps[idx_num]))
    for i in range (0, 5, 1):
        idx_num += 1
        idx_num = idx_num % 12
        temp_arr.append(float(temps[idx_num]))
    return np.average(temp_arr, axis=0)

def get_ground_water(ground_water, predict_month, district):
    temp_arr=[]
    gwater = list(ground_water[district].values())
    idx_num = month_dict[predict_month]
    temp_arr.append(gwater[idx_num])
    for i in range(0, 5, 1):
        idx_num += 1
        idx_num = idx_num % 12
        temp_arr.append(gwater[idx_num])
    print("gwater:  ", temp_arr)
    return np.average(temp_arr, axis=0)

def get_soil(soil_type):
    soil_arr=[]
    if(soil_type == 'Alluvial'):
        soil_arr.append(1)
    else:
        soil_arr.append(0)

    if(soil_type == 'Black'):
        soil_arr.append(1)
    else:
        soil_arr.append(0)

    if(soil_type == 'Loam'):
        soil_arr.append(1)
    else:
        soil_arr.append(0)

    if(soil_type == 'Red'):
        soil_arr.append(1)
    else:
        soil_arr.append(0)

    return soil_arr
        

# AI/ML parameteres default
nn_weight_path = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Server/Models/weights/kharif_crops_final.pth'
production_weight_path = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Server/Models/weights/production_weights.sav'

@app.route('/', methods=['GET'])
def index():
    return "Default Route"


@app.route('/predict', methods=['POST'])
def predict():
    content = request.json[0]
    area = content['area']
    potassium = content['potassium']
    nitrogen = content['nitrogen']
    phosphorous = content['phosphorous']
    ph = content['ph']
    crop_season = content['crop_season']
    current_crop = content['current_planted_crop']
    predict_month = content['predict_month']
    is_current = content['is_current']
    soil_type = content['soil_type']

    # Use this API for finding data using latitudes and Longitudes
    # https://climateknowledgeportal.worldbank.org/api/data/get-download-data/projection/mavg/tas/rcp26/2020_2039/21.1458$cckp$79.0882/21.1458$cckp$79.0882
    # temps stores the predicted temperature
    latitude = content['lat']
    longitude = content['lng']
    district = (content['district']).upper()
    state = (content['state']).upper()
    
    param = "tas"
    URL = "https://climateknowledgeportal.worldbank.org/api/data/get-download-data/projection/mavg/"+ param +"/rcp26/2020_2039/" + \
        latitude+"$cckp$"+longitude + "/"+latitude + "$cckp$"+longitude + ""
    
    
    resp = requests.get(url=URL)
    decoded = resp.content.decode("utf-8")
    cr = csv.reader(decoded.splitlines(), delimiter=',')
    my_list = list(cr)
    temps = []
    for index, row in enumerate(my_list):
        if index == 0:
            continue
        if index > 13:
            break
        temps.append(row[0])
    #print(temps)

    param = "pr"
    resp = requests.get(url=URL)
    decoded = resp.content.decode("utf-8")
    cr = csv.reader(decoded.splitlines(), delimiter=',')
    my_list = list(cr)
    rainfall = []
    for index, row in enumerate(my_list):
        if index == 0:
            continue
        if index > 13:
            break
        rainfall.append(row[0])
    #print(rainfall)

    # Getting the current temperature (if Current=true in Input)
    current_weather_url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude +"&lon="+longitude +"&APPID=b9bb7acaa4566f8f7de584f90c2b12c2"
    resp = requests.get(current_weather_url)
    decoded = resp.content.decode("utf-8")
    resp = json.loads(decoded)
    current_temp = resp["main"]["temp"]
    #print(current_temp)


    # Do the prediction here using Classifier clf.
    print(crop_season)
    if(crop_season == 'kharif'):
        nn_weight_path = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Server/Models/weights/kharif_crops_final.pth'
    elif(crop_season == 'rabi'):
        nn_weight_path = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Server/Models/weights/rabi_crops_final.pth'
    elif(crop_season == 'zaid'):
        nn_weight_path = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Server/Models/weights/zaid_crops_final.pth'
    
    production_weight_path = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Server/Models/weights/production_weights.sav'

    # get avg values
    temp_avg = get_avg(temps, predict_month)
    rain_avg = get_avg(rainfall, predict_month)

    # gwater calculations
    ground_water_avg = get_ground_water(ground_water, predict_month, district)
    max_area_dist = int(max_area[district])
    # print("gwater avg: {}  max_area_dist:  {}  area:  {}".format(type(ground_water_avg), type(max_area_dist), type(area)))
    gwater_available = scale_val * (float(ground_water_avg) * float(area) ) / float(max_area_dist)
    total_water = rain_avg + gwater_available

    # sow_temp
    if(is_current):
        sow_temp = current_temp
    else:
        sow_temp = temps[month_dict[predict_month]]

    # harvest temp
    harvest_temp = temps[(month_dict[predict_month]+5)%12]

    # soil paramteres
    soil = get_soil(soil_type)

    # Create parameter list
    parameteres = torch.from_numpy(np.array([[temp_avg, ph, total_water, sow_temp, harvest_temp, nitrogen, potassium, phosphorous, soil[0], soil[1], soil[2], soil[3]]], dtype='float32'))
    

    # create model instance
    nn_model = crop_model(crop_season)
    #load weights
    nn_model.load_weights(nn_weight_path)
    #get predictions
    pred = nn_model.get_predictions(parameteres)
    pred = pred.detach().numpy()

    # get top_3 predictions
    nn_model.get_top_n_predictions(pred, 3)

    print(nn_model.max_pred_array)

    # Crop Price Prediction
    crop = [str(nn_model.max_pred_array[0][1]), str(nn_model.max_pred_array[1][1]), str(nn_model.max_pred_array[2][1])]
    price_model = Production(crop, int(area), production_weight_path)

    #calculate the production and price and also display
    price_model.calculate_production_price() 

    print(price_model.prod_arr)



    # Making the response message
    response = {
        "predict": [
            {
                "crop": nn_model.max_pred_array[0][1],
                "yield_percent": nn_model.max_pred_array[0][0],
                "production": price_model.prod_arr[0][0],
                "price": price_model.prod_arr[0][1]
            },
            {
                "crop": nn_model.max_pred_array[1][1],
                "yield_percent": nn_model.max_pred_array[1][0],
                "production": price_model.prod_arr[1][0],
                "price": price_model.prod_arr[1][1]
            },
            {
                "crop": nn_model.max_pred_array[2][1],
                "yield_percent": nn_model.max_pred_array[2][0],
                "production": price_model.prod_arr[2][0],
                "price": price_model.prod_arr[2][1]
            }
        ]
    }
    return jsonify(response)


if __name__ == '__main__':

    f = open('../dataset/ground_water_dic.pkl','rb')
    ground_water = pickle.load(f)
    f.close()
    
    f = open('../dataset/max_area_groundwater.pkl','rb')
    max_area = pickle.load(f)
    f.close()
    
    app.run(host="instance-flask-app2",port=int("5000"),debug=True) 