import pytest
from src.config import url

import requests

BASE_URL = url

def test_crop_dimensions_out_of_bound_for_y():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": "http://cdn.mos.cms.futurecdn.net/iC7HBvohbJqExqvbKcV3pP.jpg", "x_start": 0, "y_start": -1, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 400
    
    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": "http://cdn.mos.cms.futurecdn.net/iC7HBvohbJqExqvbKcV3pP.jpg", "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 999999999999})
    assert upload_photo_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")


def test_crop_dimensions_out_of_bound_for_x():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": "http://cdn.mos.cms.futurecdn.net/iC7HBvohbJqExqvbKcV3pP.jpg", "x_start": -1, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 400

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": "http://cdn.mos.cms.futurecdn.net/iC7HBvohbJqExqvbKcV3pP.jpg", "x_start": 0, "y_start": 0, "x_end": 999999999999, "y_end": 1})
    assert upload_photo_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")

def test_impossible_crop_dimensions():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": "http://cdn.mos.cms.futurecdn.net/iC7HBvohbJqExqvbKcV3pP.jpg", "x_start": 1, "y_start": 0, "x_end": 0, "y_end": 1})
    assert upload_photo_response.status_code == 400

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": "http://cdn.mos.cms.futurecdn.net/iC7HBvohbJqExqvbKcV3pP.jpg", "x_start": 0, "y_start": 1, "x_end": 1, "y_end": 0})
    assert upload_photo_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")

def test_not_jpg():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": 'https://i.imgur.com/US9Rn31.png', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 400

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": 'https://static.wikia.nocookie.net/onepiece/images/6/6d/Monkey_D._Luffy_Anime_Post_Timeskip_Infobox.png/revision/latest?cb=20200429191518', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")

def test_error_retrieving_image():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": 'https://fakeurlfighdfuioghfuifhgulsfh', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")

def test_user_upload_profile_image_success():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": 'https://i.pinimg.com/550x/16/77/42/167742f17fc9b58a6e1084618d244196.jpg', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")

def test_invalid_token():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": "invalid_token", "img_url": 'https://i.pinimg.com/550x/16/77/42/167742f17fc9b58a6e1084618d244196.jpg', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 403

    requests.delete(f"{BASE_URL}clear/v1")

def test_user_reupload_profile_image_success():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": 'https://i.pinimg.com/550x/16/77/42/167742f17fc9b58a6e1084618d244196.jpg', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 200

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": 'https://upload.wikimedia.org/wikipedia/commons/4/41/Sunflower_from_Silesia2.jpg', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")

def test_user_change_handle_with_non_default_profile_image():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']

    upload_photo_response = requests.post(f"{BASE_URL}user/profile/uploadphoto/v1", json={"token": register_token, "img_url": 'https://i.pinimg.com/550x/16/77/42/167742f17fc9b58a6e1084618d244196.jpg', "x_start": 0, "y_start": 0, "x_end": 1, "y_end": 1})
    assert upload_photo_response.status_code == 200

    new_handle = 'newhandle'
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")
