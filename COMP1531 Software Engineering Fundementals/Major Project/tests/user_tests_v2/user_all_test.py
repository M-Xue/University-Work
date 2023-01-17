import pytest
from src.other import clear_v1
from src.config import url

import requests

BASE_URL = url

def test_one_user():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    response = requests.get(f"{BASE_URL}users/all/v1?token={register_token}")
    assert response.status_code == 200
    response_details = response.json()
    assert response_details['users'][0]['u_id'] == register_u_id
    assert response_details['users'][0]['email'] == "valid-email@gmail.com"
    assert response_details['users'][0]['name_first'] == "John"
    assert response_details['users'][0]['name_last'] == "Smith"
    requests.delete(f"{BASE_URL}clear/v1")


def test_multiple_users():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email1@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    response = requests.get(f"{BASE_URL}users/all/v1?token={register_token}") 
    assert response.status_code == 200
    response_details = response.json()
    register_u_id2 = register_response2.json()['auth_user_id']

    assert response_details['users'][0]['u_id'] == register_u_id
    assert response_details['users'][0]['email'] == "valid-email@gmail.com"
    assert response_details['users'][0]['name_first'] == "John"
    assert response_details['users'][0]['name_last'] == "Smith"

    assert response_details['users'][1]['u_id'] == register_u_id2
    assert response_details['users'][1]['email'] == "valid-email1@gmail.com"
    assert response_details['users'][1]['name_first'] == "John"
    assert response_details['users'][1]['name_last'] == "Smith"
    requests.delete(f"{BASE_URL}clear/v1")

def test_token_is_wrong():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    response = requests.get(f"{BASE_URL}users/all/v1?token={register_token}X") 
    assert response.status_code == 403
    response = requests.get(f"{BASE_URL}users/all/v1?token={register_token[::-1]}") 
    assert response.status_code == 403
    response = requests.get(f"{BASE_URL}users/all/v1?token={register_token[:-1]}") 
    assert response.status_code == 403
    requests.delete(f"{BASE_URL}clear/v1")


def test_no_token():
    requests.delete(f"{BASE_URL}clear/v1")
    requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    response = requests.get(f"{BASE_URL}users/all/v1?token=") 
    assert response.status_code == 403
    requests.delete(f"{BASE_URL}clear/v1")