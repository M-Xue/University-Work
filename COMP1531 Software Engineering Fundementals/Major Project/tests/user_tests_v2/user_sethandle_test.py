import pytest
from src.config import url
import requests

BASE_URL = url

def test_user_successfully_found_and_edited_handle():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    old_handle = user['user']['handle_str']

    new_handle = 'newhandle'
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 200
    
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200

    assert old_handle != new_handle
    assert user['user']['handle_str'] == new_handle

    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_token = login_response.json()['token']
    login_u_id = login_response.json()['auth_user_id']
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['handle_str'] == new_handle

    requests.delete(f"{BASE_URL}clear/v1")


def test_user_successfully_found_and_edited_handle_via_login():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    old_handle = user['user']['handle_str']

    new_handle = 'newhandle'
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_token = login_response.json()['token']
    login_u_id = login_response.json()['auth_user_id']
    
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert old_handle == user['user']['handle_str']

    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": login_token,"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 200

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert new_handle == user['user']['handle_str']
    assert old_handle != new_handle

    requests.delete(f"{BASE_URL}clear/v1")


def test_multiple_users_successfully_found_and_edited_handle():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response1 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email1@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response1.status_code == 200
    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response2.status_code == 200
    register_response3 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email3@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response3.status_code == 200

    register_token = register_response1.json()['token']
    register_u_id = register_response1.json()['auth_user_id']
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    old_handle = user['user']['handle_str']

    new_handle = 'newhandle'
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 200
    
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200

    assert old_handle != new_handle
    assert user['user']['handle_str'] == new_handle

    requests.delete(f"{BASE_URL}clear/v1")


def test_user_successfully_found_invalid_handle():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    register_u_id = register_response.json()['auth_user_id']

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    assert get_profile_response.status_code == 200

    invalid_new_handle_too_short = 'xx' 
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": invalid_new_handle_too_short}) 
    assert handle_edit_response.status_code == 400
    invalid_new_handle_too_long = 'xxxxxxxxxxxxxxxxxxxxx' 
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": invalid_new_handle_too_long}) 
    assert handle_edit_response.status_code == 400
    invalid_new_handle_not_alphanumeric = '!@#$%^&*()_+-=' 
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": invalid_new_handle_not_alphanumeric}) 
    assert handle_edit_response.status_code == 400
    invalid_new_handle_not_alphanumeric = '[{]}\\|;:\',<.>/?~`' 
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": invalid_new_handle_not_alphanumeric}) 
    assert handle_edit_response.status_code == 400
    invalid_new_handle_not_alphanumeric = 'new handle' 
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": invalid_new_handle_not_alphanumeric}) 
    assert handle_edit_response.status_code == 400
    invalid_new_handle_not_alphanumeric = 'newh@ndle' 
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": invalid_new_handle_not_alphanumeric}) 
    assert handle_edit_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")


def test_user_successfully_found_taken_handle():
    requests.delete(f"{BASE_URL}clear/v1")

    new_handle = 'newhandle'

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_token = register_response.json()['token']
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token,"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 200

    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email1@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response2.status_code == 200
    register_token2 = register_response2.json()['token']
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token2,"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")


def test_token_is_wrong():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']

    new_handle = 'newhandle'
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token + 'X',"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 403
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token[::-1],"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 403
    handle_edit_response = requests.put(f"{BASE_URL}user/profile/sethandle/v1", json={"token": register_token[:-1],"handle_str": new_handle}) 
    assert handle_edit_response.status_code == 403

    requests.delete(f"{BASE_URL}clear/v1")
