#importing the required libraries
import pandas as pd
import numpy as np
from scipy.sparse.construct import random
from sklearn.model_selection import train_test_split

#Reading the csv file
data=pd.read_csv('../dataset/final_dataset.csv')
#data=pd.read_csv('../dataset/temp_humid_crop.csv')
print(data.head(1))
#print(data.crop)

# Creating dummy variable for target i.e label
label= pd.get_dummies(data.crop).iloc[: , 1:]
#print(label)
data= pd.concat([data,label],axis=1)
#print(data)
data.drop('crop', axis=1,inplace=True)
#print(data)
print('The data present in one row of the dataset is')
print(data.head(1))
train=data.iloc[:, 6:9].values
test=data.iloc[: ,9:].values

#Dividing the data into training and test set
X_train,X_test,y_train,y_test=train_test_split(train,test,test_size=0.3,random_state=102)

# from sklearn.preprocessing import StandardScaler
# sc = StandardScaler()
# X_train = sc.fit_transform(X_train)
# X_test = sc.transform(X_test)

# #Importing Decision Tree classifier
# from sklearn.tree import DecisionTreeRegressor
# clf=DecisionTreeRegressor()

from sklearn.ensemble import RandomForestRegressor
clf = RandomForestRegressor()

#Fitting the classifier into training set
clf.fit(X_train,y_train)
pred=clf.predict(X_test)

print(np.argmax(pred[0]))
print(X_test[0], pred[0] , y_test[0])

from sklearn.metrics import accuracy_score
# Finding the accuracy of the model
a=accuracy_score(np.argmax(y_test,axis=1),np.argmax(pred,axis=1))
print("The accuracy of this model is: ", a*100)