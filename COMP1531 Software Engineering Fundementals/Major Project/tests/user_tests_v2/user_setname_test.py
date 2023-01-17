import pytest
from src.config import url
import requests

BASE_URL = url

def test_one_user_success():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    new_first_name = 'newFirstName'
    new_last_name = 'newLastName'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 200
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == new_first_name
    assert user['user']['name_last'] == new_last_name
    requests.delete(f"{BASE_URL}clear/v1")


def test_one_user_success_via_login():
    requests.delete(f"{BASE_URL}clear/v1")
    requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_token = login_response.json()['token']
    login_u_id = login_response.json()['auth_user_id']

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == login_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    new_first_name = 'newFirstName'
    new_last_name = 'newLastName'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": login_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 200
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == login_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == new_first_name
    assert user['user']['name_last'] == new_last_name
    requests.delete(f"{BASE_URL}clear/v1")


def test_user_successfully_found_invalid_first_name():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    new_first_name = 'newFirstNamexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'
    new_last_name = 'newLastName'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 400
    new_first_name = ''
    new_last_name = 'newLastName'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")


def test_user_successfully_found_invalid_last_name():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    new_first_name = 'newFirstName' 
    new_last_name = 'newLastNamexxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 400
    new_first_name = 'newFirstName' 
    new_last_name = ''
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")


def test_multiple_users_successfully_found_and_edited_name():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2@gmail.com", "password": "valid-password","name_first": "John2","name_last": "Smith2"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    new_first_name = 'newFirstName'
    new_last_name = 'newLastName'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 200
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email@gmail.com"
    assert user['user']['name_first'] == new_first_name
    assert user['user']['name_last'] == new_last_name

    register_token = register_response2.json()['token']
    register_u_id = register_response2.json()['auth_user_id']

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email2@gmail.com"
    assert user['user']['name_first'] == "John2"
    assert user['user']['name_last'] == "Smith2"

    new_first_name2 = 'newFirstName2'
    new_last_name2 = 'newLastName2'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token,"name_first": new_first_name2,"name_last": new_last_name2}) 
    assert name_edit_response.status_code == 200
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == "valid-email2@gmail.com"
    assert user['user']['name_first'] == new_first_name2
    assert user['user']['name_last'] == new_last_name2
    requests.delete(f"{BASE_URL}clear/v1")


def test_token_is_wrong():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']

    new_first_name = 'newFirstName2'
    new_last_name = 'newLastName2'
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token + 'X',"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 403
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token[::-1],"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 403
    name_edit_response = requests.put(f"{BASE_URL}user/profile/setname/v1", json={"token": register_token[:-1],"name_first": new_first_name,"name_last": new_last_name}) 
    assert name_edit_response.status_code == 403

    requests.delete(f"{BASE_URL}clear/v1")
