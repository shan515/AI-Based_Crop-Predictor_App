import pickle

crops = ["arhar/tur","bajra","barley","coriander","cotton_lint","cowpea_lobia","dry_chillies","garlic",
"ginger","gram","groundnut","jowar","linseed","maize-k","maize-r","masoor","moong_green_gram","onion","peas_and_beans_pulses",
"potato","ragi","rapeseed &mustard","rice","safflower","sugarcane","sunflower","turmeric","urad","wheat"]


crops_rates = {"arhar/tur":6000,
"bajra":2150,
"barley":1600,
"coriander":0,
"cotton_lint":5515,
"cowpea_lobia":0,
"dry_chillies":0,
"garlic":0,
"ginger":0,
"gram":4875,
"groundnut":5275,
"jowar":2640,
"linseed":0,
"maize-k":1850,
"maize-r":0,
"masoor":4800,
"moong_green_gram":7196,
"onion":500,
"peas_and_beans_pulses":0,
"potato":400,
"ragi":3295,
"rapeseed &mustard":4425,
"rice":1868,
"safflower":5215,
"sugarcane":285,
"sunflower":5885,
"turmeric":6500,
"urad":6000,
"wheat":1925}

#filepath = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Models/production/'
#filename = "production_weights.sav"
#pickle.dump(model_list, open(filepath+filename, 'wb'))



class Production:
    def __init__(self,crop,area,filename):
        self.prod_arr=[]
        self.area=area
        self.crop=crop
        self.model_list=pickle.load(open(filename, 'rb'))
        
    def single_production_price(self,crop_name):
        prod=self.model_list[crops.index(crop_name)].predict([[self.area]])
        production=round(prod[0,0],2)   #Tonnes
        price = crops_rates[crop_name] * production * 10   #tonnes converted to quintal here
        return [production,price]
        
    def display(self,crop_name):
        crop_production=self.prod_arr[-1][0]
        crop_price=self.prod_arr[-1][1]
        print('crop {} production is {}tonnes:'.format(crop_name,crop_production))
        print('crop {} price is Rs.{}:'.format(crop_name,crop_price))
    
    def calculate_production_price(self):
        for c in self.crop:
            self.prod_arr.append(self.single_production_price(c))
            self.display(c)
