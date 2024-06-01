import os
import pickle
import time

import flask
import imageio
import matplotlib.pyplot as plt
import numpy
import scipy
import werkzeug
from flask import Flask, request,render_template
from flask_restful import Api, Resource
import pandas as pd
import numpy as np
import tensorflow as tf
from pelican.plugins.image_process import process_image

from project import *
from pose_estimatin import *

compat = tf.compat.v1.ConfigProto()
compat.gpu_options.allow_growth = True

session = tf.compat.v1.Session(config=compat)

loaded_model = tf.keras.models.load_model("myModel")
#
app = Flask(__name__)
api = Api(app)

def correction(pose_name):
    """
    Corrects angles related to a particular pose.
    :param pose_name: The name of the pose to correct angles for.
    :return: A dictionary containing correction message for the given
    pose.
    """
    pickle_dict = {"warrior2": 'warrior2_pickle', "cobra": 'cobra_pickle', 'dog': 'dog_pickle', 'tree': 'tree_pickle'}
    angle_calculation('pose_data', 'user_posture', postors_angles[pose_name])
    angle_calculation(pose_name, pickle_dict[pose_name], postors_angles[pose_name], True)
    good_angles, input_angles = get_nearest_idx('user_posture', pickle_dict[pose_name], pose_name)
    return fix_angles(good_angles, input_angles, pose_name)

@app.route('/search/<string:searchKey>', methods=['GET', 'POST'])
def handle_search_request(pose_name):
    if request.method == 'GET':
        return correction(pose_name)
    else:
        return "Problem on server side"

def get_label(img, filename):
    img.save("data/" + filename)
    images_in_new = "data/"
    images_out_new = 'poses_images_new'
    csvs_out_new = 'new.csv'
    X_test, y_test, _, df_test = load_pose_landmarks(csvs_out_test_path)
    preprocessor = YogaPreprocessor(
        images_in_new,
        images_out_new,
        csvs_out_new
    )
    preprocessor.process()
    X_new, y_new, a, dataframe = load_pose_landmarks(csvs_out_new)
    args = np.zeros(len(_))
    best_line = 0
    j = -1
    for i, line in enumerate(loaded_model.predict(X_new)):
        place = np.argmax(line)
        if line[place] > best_line:
            best_line = line[place]
            j = i
        if (line[place] > 0.9):
            args[place] += 1

    image_names = (
        [n for n in os.listdir('data/') if not n.startswith('.')])
    print(X_new)
    print(np.argmax(args))
    X_new.iloc[[np.argmax(args)]].to_pickle("new1")
    return _[np.argmax(args)], image_names[j], correction(_[np.argmax(args)])

@app.route('/', methods=[ 'POST','GET'])
def handle_recognize_request():
    if request.method == 'POST':
        imagefile = flask.request.files['image']
        filename = werkzeug.utils.secure_filename(imagefile.filename)
        print("\nReceived image File name : " + imagefile.filename)
        try:
            predicted_label, _, _ = get_label(imagefile, filename)
        except:
            print("unrecognize person")
            return {"err": "unrecognize person"}
        print(predicted_label)

        return correction(predicted_label)
    else:
        return render_template('index.html')
if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0')
