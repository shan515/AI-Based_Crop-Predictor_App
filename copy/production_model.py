from sklearn.linear_model import LinearRegression
import pandas as pd
import numpy as np
import pickle


# Crops list
crops = ["arhar/tur", "bajra", "barley", "coriander", "cotton_lint", "cowpea_lobia", "dry chillies", "garlic", "ginger", "gram", "groundnut", 
"jowar", "linseed", "maize-k", "maize-r", "masoor", "moong_green_gram", "onion", "peas_and_beans_pulses", "potato", "ragi", "rapeseed &mustard", "rice",
"safflower", "sugarcane", "sunflower", "turmeric", "urad", "wheat"]

class prod_model():
    def load_weights(self, filepath, filename):
        return pickle.load(open(filepath+filename, 'rb'))



filepath = '/home/sravanchittupalli/konnoha/clones/Lets_HackIT/Models/production/'
filename = "production_weights.sav"

model = prod_model()
model = model.load_weights(filepath, filename)
print(model[crops.index('masoor')].predict([[2745]]))