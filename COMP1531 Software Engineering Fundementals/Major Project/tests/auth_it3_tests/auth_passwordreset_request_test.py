import pytest
from src.other import clear_v1
from src.config import url

import requests

BASE_URL = url

def test_user_sessions_invalidated():
    
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}") 
    registered_user = response.json()
    assert response.status_code == 200

    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    assert login_response.status_code == 200
    login_token = login_response.json()['token']
    login_u_id = login_response.json()['auth_user_id']

    assert login_u_id == register_u_id

    response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}") 
    logged_in_user = response.json()
    assert response.status_code == 200

    assert logged_in_user['user']['email'] == "valid-email@gmail.com"
    assert registered_user['user']['email'] == "valid-email@gmail.com"

    email_request_response = requests.post(f"{BASE_URL}auth/passwordreset/request/v1", json={'email': 'valid-email@gmail.com'})
    assert email_request_response.status_code == 200

    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}") 
    registered_user = response.json()
    assert response.status_code == 403

    response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}") 
    logged_in_user = response.json()
    assert response.status_code == 403

    requests.delete(f"{BASE_URL}clear/v1")


def test_invalid_email():
    requests.delete(f"{BASE_URL}clear/v1")

    email_request_response = requests.post(f"{BASE_URL}auth/passwordreset/request/v1", json={'email': 'unregistered-email@gmail.com'})
    assert email_request_response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")


def test_other_user_sessions_uneffected():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200

    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response2.status_code == 200
    register_token2 = register_response2.json()['token']
    register_u_id2 = register_response2.json()['auth_user_id']

    email_request_response = requests.post(f"{BASE_URL}auth/passwordreset/request/v1", json={'email': 'valid-email@gmail.com'})
    assert email_request_response.status_code == 200

    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token2}&u_id={register_u_id2}") 
    assert response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")

def test_two_requests():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200

    email_request_response = requests.post(f"{BASE_URL}auth/passwordreset/request/v1", json={'email': 'valid-email@gmail.com'})
    assert email_request_response.status_code == 200
    email_request_response = requests.post(f"{BASE_URL}auth/passwordreset/request/v1", json={'email': 'valid-email@gmail.com'})
    assert email_request_response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")


def test_two_differnet_user_requests():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200

    email_request_response = requests.post(f"{BASE_URL}auth/passwordreset/request/v1", json={'email': 'valid-email@gmail.com'})
    assert email_request_response.status_code == 200


    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response2.status_code == 200
    
    email_request_response = requests.post(f"{BASE_URL}auth/passwordreset/request/v1", json={'email': 'valid-email2@gmail.com'})
    assert email_request_response.status_code == 200

    requests.delete(f"{BASE_URL}clear/v1")
