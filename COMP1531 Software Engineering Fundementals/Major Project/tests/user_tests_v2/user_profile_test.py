import pytest
from src.config import url
import requests

BASE_URL = url

def test_user_successfully_found():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}") 
    user = response.json()
    assert response.status_code == 200

    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"
    requests.delete(f"{BASE_URL}clear/v1")


def test_multiple_users_successfully_found():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email1@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']
    register_token2 = register_response2.json()['token']
    register_u_id2 = register_response2.json()['auth_user_id']

    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}") 
    user = response.json()
    assert response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token2}&u_id={register_u_id2}") 
    user = response.json()
    assert response.status_code == 200
    assert user['user']['u_id'] == register_u_id2
    assert user['user']['email'] == "valid-email1@gmail.com"
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"
    requests.delete(f"{BASE_URL}clear/v1")


def test_token_is_wrong():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']
    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}") 
    assert response.status_code == 200
    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}X&u_id={register_u_id}") 
    assert response.status_code == 403
    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token[::-1]}&u_id={register_u_id}") 
    assert response.status_code == 403
    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token[:-1]}&u_id={register_u_id}") 
    assert response.status_code == 403
    requests.delete(f"{BASE_URL}clear/v1")


def test_no_token():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_u_id = register_response.json()['auth_user_id']
    response = requests.get(f"{BASE_URL}user/profile/v1?token=&u_id={register_u_id}") 
    assert response.status_code == 403
    requests.delete(f"{BASE_URL}clear/v1")

def test_non_existant_u_id():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id=999") 
    assert response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")


def test_no_u_id():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id=") 
    assert response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")
