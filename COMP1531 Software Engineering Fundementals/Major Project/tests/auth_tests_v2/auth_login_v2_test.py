import pytest
from src.other import clear_v1
from src.config import url

import requests

BASE_URL = url
SECRET = 'COMP1531'


def test_unregistered_email():
    requests.delete(f"{BASE_URL}clear/v1")
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'unregistered-email@gmail.com','password' :'xxxxxx'})
    assert response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")

def test_invalid_email_format():
    requests.delete(f"{BASE_URL}clear/v1")
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'non-existant-email@gmail','password' :'xxxxxx'})
    assert response.status_code == 400
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'non-existant-email','password' :'xxxxxx'})
    assert response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")


def test_wrong_password():
    requests.delete(f"{BASE_URL}clear/v1")
    requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'user1@gmail.com', 'password': 'right-password','name_first': 'John','name_last': 'Smith'})
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'user1@gmail.com','password' :'wrong-password'})
    assert response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")


def test_correct_login_details():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'user1@gmail.com', 'password': 'right-password','name_first': 'John','name_last': 'Smith'})
    register_response_data = register_response.json()
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'user1@gmail.com','password' :'right-password'})
    response_data = response.json()
    assert response_data['auth_user_id'] == register_response_data['auth_user_id']
    assert response.status_code == 200

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'user2@gmail.com', 'password': 'right-password','name_first': 'John','name_last': 'Smith'})
    register_response_data = register_response.json()
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'user2@gmail.com','password' :'right-password'})
    response_data = response.json()
    assert response_data['auth_user_id'] == register_response_data['auth_user_id']
    assert response.status_code == 200
    requests.delete(f"{BASE_URL}clear/v1")


def test_multiple_users_can_be_logged_into():
    requests.delete(f"{BASE_URL}clear/v1")
    user_successful_login_arr = []
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'valid-email@gmail.com', 'password': 'valid-password','name_first': 'John','name_last': 'Smith'})
    register_response_data = register_response.json()
    user1_id = register_response_data['auth_user_id']
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'valid-email2@gmail.com', 'password': 'valid-password','name_first': 'John','name_last': 'Smith'})
    register_response_data = register_response.json()
    user2_id = register_response_data['auth_user_id']
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'valid-email3@gmail.com', 'password': 'valid-password','name_first': 'John','name_last': 'Smith'})
    register_response_data = register_response.json()
    user3_id = register_response_data['auth_user_id']

    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    response_data = response.json()
    user_successful_login_arr.append(response_data['auth_user_id'] == user1_id) 
    assert response.status_code == 200

    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email2@gmail.com','password' :'valid-password'})
    response_data = response.json()
    user_successful_login_arr.append(response_data['auth_user_id'] == user2_id) 
    assert response.status_code == 200

    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email3@gmail.com','password' :'valid-password'})
    response_data = response.json()
    user_successful_login_arr.append(response_data['auth_user_id'] == user3_id) 
    assert response.status_code == 200

    assert all(user_successful_login_arr)
    requests.delete(f"{BASE_URL}clear/v1")


def test_non_alphanumeric_names_and_passwords_registration_users_can_be_logged_into():
    requests.delete(f"{BASE_URL}clear/v1")
    user_successful_login_arr = []
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'valid-email@gmail.com', 'password': 'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?','name_first': 'John','name_last': 'Smith'})
    register_response_data = register_response.json()
    user1_id = register_response_data['auth_user_id']
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'valid-email2@gmail.com', 'password': 'valid-password','name_first': 'John`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?','name_last': 'Smith'})
    register_response_data = register_response.json()
    user2_id = register_response_data['auth_user_id']
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'valid-email3@gmail.com', 'password': 'valid-password','name_first': 'John','name_last': 'Smith`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?'})
    register_response_data = register_response.json()
    user3_id = register_response_data['auth_user_id']
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={'email': 'valid-email4@gmail.com', 'password': 'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?','name_first': 'John`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?','name_last': 'Smith`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?'})
    register_response_data = register_response.json()
    user4_id = register_response_data['auth_user_id']


    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?'})
    response_data = response.json()
    user_successful_login_arr.append(response_data['auth_user_id'] == user1_id) 
    assert response.status_code == 200
    
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email2@gmail.com','password' :'valid-password'})
    response_data = response.json()
    user_successful_login_arr.append(response_data['auth_user_id'] == user2_id) 
    assert response.status_code == 200
    
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email3@gmail.com','password' :'valid-password'})
    response_data = response.json()
    user_successful_login_arr.append(response_data['auth_user_id'] == user3_id) 
    assert response.status_code == 200
    
    response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email4@gmail.com','password' :'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?'})
    response_data = response.json()
    user_successful_login_arr.append(response_data['auth_user_id'] == user4_id) 
    assert response.status_code == 200
    
    assert all(user_successful_login_arr)
    requests.delete(f"{BASE_URL}clear/v1")



