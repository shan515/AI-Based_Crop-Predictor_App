# Improved model
import torch
import numpy as np
import pandas as pd
from torch import nn

#flags
debug = False

# kharif crops list
k_crops = ["arhar/tur","bajra","cotton_lint","groundnut","maize-k","moong_green_gram)","ragi",
"rice","sunflower","urad","cowpea_lobia","ginger"]

# rabi crops list
r_crops = ["barley","gram","jowar","linseed","maize-r","masoor",
"peas_and_beans_pulses","rapeseed &mustard","safflower",
"wheat","garlic"]

# zaid crops list
z_crops = ["coriander","dry_chillies","onion","potato","sugarcane","turmeric"]



# KHARIF neural network parameteres
k_input_nodes = 12
k_hidden1_nodes = 64
k_hidden2_nodes = 64
k_output_nodes = 12

# RABI neural network parameteres
r_input_nodes = 12
r_hidden1_nodes = 64
r_hidden2_nodes = 64
r_output_nodes = 11

# ZAID neural network parameteres
z_input_nodes = 12
z_hidden1_nodes = 64
z_hidden2_nodes = 64
z_output_nodes = 6


# creating neural net
class crop_model(nn.Module):
    def __init__(self, season):
        super().__init__()
        self.season = season

        if(season == 'Kharif'):
            df = pd.read_csv('kharif_all_crops.csv')
            self.input = nn.Linear(k_input_nodes, k_hidden1_nodes)
            self.hidden1 = nn.Linear(k_hidden1_nodes, k_hidden2_nodes)
            self.hidden2 = nn.Linear(k_hidden2_nodes, k_output_nodes)
        if(season == 'Rabi'):
            df = pd.read_csv('rabi_crops.csv')
            self.input = nn.Linear(r_input_nodes, r_hidden1_nodes)
            self.hidden1 = nn.Linear(r_hidden1_nodes, r_hidden2_nodes)
            self.hidden2 = nn.Linear(r_hidden2_nodes, r_output_nodes)
        if(season == 'Zaid'):
            df = pd.read_csv('zaid_crops.csv')
            self.input = nn.Linear(z_input_nodes, z_hidden1_nodes)
            self.hidden1 = nn.Linear(z_hidden1_nodes, z_hidden2_nodes)
            self.hidden2 = nn.Linear(z_hidden2_nodes, z_output_nodes)
            
        self.sigmoid = nn.Sigmoid()
        self.softmax = nn.Softmax()
        self.relu = nn.ReLU()

        self.max_pred_array = []

        
        np_inputs = df.to_numpy()
        inputs = np_inputs[:, 0:12]
        inputs = np.array(inputs, dtype='float32')

        from sklearn.preprocessing import StandardScaler
        self.scaler = StandardScaler()
        self.scaler.fit(inputs)

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
        return self(self.scaler.transform(parameteres))

    def get_top_n_predictions(self, pred, n):
        for i in pred:
            for j in range(0, n, 1):
                temp = -1
                temp_index = -1
                for k in range (0, len(i), 1):
                    if ( i[k] > temp):
                        temp = i[k]
                        temp_index = k
                i[temp_index] = -1
                if(self.season == 'Kharif'):
                    self.max_pred_array.append([temp*100, k_crops[temp_index]])
                if(self.season == 'Rabi'):
                    self.max_pred_array.append([temp*100, r_crops[temp_index]])
                if(self.season == 'Zaid'):
                    self.max_pred_array.append([temp*100, z_crops[temp_index]])




