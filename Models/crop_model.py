# Improved model
import torch
import numpy as np
import pandas as pd
from torch import nn

#flags
debug = False

# Crops list
crops = ["arhar/tur", "bajra", "barley", "coriander", "cotton_lint", "cowpea_lobia", "dry chillies", "garlic", "ginger", "gram", "groundnut", 
"jowar", "linseed", "maize-k", "maize-r", "masoor", "moong_green_gram", "onion", "peas_and_beans_pulses", "potato", "ragi", "rapeseed &mustard", "rice",
"safflower", "sugarcane", "sunflower", "turmeric", "urad", "wheat"]



# defining neural network parameteres
input_nodes = 8
hidden1_nodes = 32
hidden2_nodes = 64
output_nodes = 29

# creating neural net
class crop_model(nn.Module):
    def __init__(self):
        super().__init__()

        self.input = nn.Linear(input_nodes, hidden1_nodes)
        self.hidden1 = nn.Linear(hidden1_nodes, hidden2_nodes)
        self.hidden2 = nn.Linear(hidden2_nodes, output_nodes)
        self.sigmoid = nn.Sigmoid()
        self.softmax = nn.Softmax()
        self.relu = nn.ReLU()

    def forward(self, x):
        x = self.input(x)
        x = self.relu(x)
        x = self.hidden1(x)
        x = self.relu(x)
        x = self.hidden2(x)
        x = self.softmax(x)
        return x

    def load_weights(self, path):
        self.load_state_dict(torch.load(path))

    def get_predictions(self, parameteres):
        return self(parameteres)

max_pred_array=[]
def get_top_n_predictions(pred, n):
    for i in pred:
        for j in range(0, n, 1):
            temp = -1
            temp_index = -1
            for k in range (0, len(i), 1):
                if ( i[k] > temp):
                    temp = i[k]
                    temp_index = k
            i[temp_index] = -1
            max_pred_array.append([temp*100, crops[temp_index]])



# example parameteres
path = 'nn/crop_prediction_weights_final_best_trained.pth'
parameteres = torch.from_numpy(np.array([[30, 7, 65, 25, 35, 40, 15, 0]], dtype='float32'))

# create model instance
model = crop_model()

#load weights
model.load_weights(path)
if debug:
    print(list(model.parameters()))

#get predictions
pred = model.get_predictions(parameteres)
pred = pred.detach().numpy()
if debug:
    print(pred)

get_top_n_predictions(pred, 3)
print(max_pred_array)

