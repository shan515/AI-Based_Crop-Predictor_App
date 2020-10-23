from flask import Flask
import joblib

app = Flask(__name__)

@app.route('/',methods=['GET'])
def index():
    return "Hello World"

@app.route('/predict',methods=['GET'])
def predict():
    clf = joblib.load('../Models/saved_model.sav')
    # Prediction using the extracted params to be done here
    return "Predicted Results"

if __name__ == '__main__':
    app.run()