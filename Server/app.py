from flask import Flask
from flask import request
from flask import jsonify
import joblib
import csv
import requests
import json
import pickle
app = Flask(__name__)


@app.route('/', methods=['GET'])
def index():
    return "Default Route"


@app.route('/predict', methods=['POST'])
def predict():
    area = request.form.get('area')
    sowT = request.form.get('sowT')
    pot = request.form.get('potassium')
    nit = request.form.get('nitrogen')
    phosphorous = request.form.get('phosphorous')
    ph = request.form.get('ph')
    crop_season = request.form.get('crop_season')
    current_crop = request.form.get('current_planted_crop')
    predict_month = request.form.get('predict_month')
    current_crop_month = request.form.get('current_crop_month')

    # Use this API for finding data using latitudes and Longitudes
    # https://climateknowledgeportal.worldbank.org/api/data/get-download-data/projection/mavg/tas/rcp26/2020_2039/21.1458$cckp$79.0882/21.1458$cckp$79.0882
    # temps stores the predicted temperature
    latitude = str(request.form.get('latitude'))
    longitude = str(request.form.get('longitude'))
    
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
    print(temps)

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
    print(rainfall)

    # Getting the current temperature (if Current=true in Input)
    current_weather_url = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude +"&lon="+longitude +"&APPID=b9bb7acaa4566f8f7de584f90c2b12c2"
    resp = requests.get(current_weather_url)
    decoded = resp.content.decode("utf-8")
    resp = json.loads(decoded)
    current_temp = resp["main"]["temp"]
    print(current_temp)

    # Do the prediction here using Classifier clf.

    # Making the response message
    response = {
        "predict": [
            {
                "crop": "Predicted Crop 1",
                "yield_percent": 90
            },
            {
                "crop": "Predicted Crop 2",
                "yield_percent": 50
            }
        ]
    }
    return jsonify(response)


if __name__ == '__main__':
    clf = joblib.load('../Models/saved_model.sav')
    
    f = open('../dataset/ground_water_dic.pkl','rb')
    ground_water = pickle.load(f)
    f.close()
    
    f = open('../dataset/max_area_groundwater.pkl','rb')
    max_area = pickle.load(f)
    f.close()
    
    app.run()