import pytest
from src.other import clear_v1
from src.config import url

import requests

BASE_URL = url

def test_user_successfully_logout():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    register_token = register_response_data["token"]
    register_u_id = register_response_data["auth_user_id"]

    logout_response = requests.post(f"{BASE_URL}auth/logout/v1", json={"token": register_token})
    assert logout_response.status_code == 200

    get_all_users_response = requests.get(f"{BASE_URL}users/all/v1?token={register_token}")
    assert get_all_users_response.status_code == 403

    get_user_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}") 
    assert get_user_profile_response.status_code == 403

    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token,"email": "new_email@gmail.com"}) 
    assert email_edit_response.status_code == 403

    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": "new_handle"}) 
    assert handle_edit_response.status_code == 403

    new_first_name = 'newFirstName'
    new_last_name = 'newLastName'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 403

    requests.delete(f"{BASE_URL}clear/v1")

def test_non_logged_out_sessions_valid():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    register_token = register_response_data["token"]
    register_response_data["auth_user_id"]

    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_response_data = login_response.json()
    assert login_response_data['auth_user_id'] == register_response_data['auth_user_id']
    assert login_response.status_code == 200

    # Bunch of extra sessions
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_response_data = login_response.json()
    assert login_response_data['auth_user_id'] == register_response_data['auth_user_id']
    assert login_response.status_code == 200
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_response_data = login_response.json()
    assert login_response_data['auth_user_id'] == register_response_data['auth_user_id']
    assert login_response.status_code == 200
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_response_data = login_response.json()
    assert login_response_data['auth_user_id'] == register_response_data['auth_user_id']
    assert login_response.status_code == 200
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_response_data = login_response.json()
    assert login_response_data['auth_user_id'] == register_response_data['auth_user_id']
    assert login_response.status_code == 200


    logout_response = requests.post(f"{BASE_URL}auth/logout/v1", json={"token": register_token})
    assert logout_response.status_code == 200

    get_all_users_response = requests.get(f"{BASE_URL}users/all/v1?token={register_token}")
    assert get_all_users_response.status_code == 403


    get_all_users_response = requests.get(f"{BASE_URL}users/all/v1?token={login_response_data['token']}")
    assert get_all_users_response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")

def test_invalid_token():
    requests.delete(f"{BASE_URL}clear/v1")

    logout_response = requests.post(f"{BASE_URL}auth/logout/v1", json={"token": 'hi'})
    assert logout_response.status_code == 403

def test_logging_out_with_invalid_token():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    register_token = register_response_data["token"]

    logout_response = requests.post(f"{BASE_URL}auth/logout/v1", json={"token": register_token})
    assert logout_response.status_code == 200

    logout_response = requests.post(f"{BASE_URL}auth/logout/v1", json={"token": register_token})
    assert logout_response.status_code == 403

    requests.delete(f"{BASE_URL}clear/v1")
