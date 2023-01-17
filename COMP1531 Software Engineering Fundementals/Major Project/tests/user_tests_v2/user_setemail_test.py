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

    new_email = 'new-email@gmail.com'
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token,"email": new_email}) 
    assert email_edit_response.status_code == 200

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == new_email
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': new_email,'password' :'valid-password'})
    login_token = login_response.json()['token']
    login_u_id = login_response.json()['auth_user_id']
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == login_u_id
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == new_email
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    requests.delete(f"{BASE_URL}clear/v1")


def test_one_user_success_via_login():
    requests.delete(f"{BASE_URL}clear/v1")

    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_u_id = register_response.json()['auth_user_id']
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': 'valid-email@gmail.com','password' :'valid-password'})
    login_token = login_response.json()['token']
    login_u_id = login_response.json()['auth_user_id']

    new_email = 'new-email@gmail.com'
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": login_token,"email": new_email}) 
    assert email_edit_response.status_code == 200

    login_response2 = requests.post(f"{BASE_URL}auth/login/v2", json={'email': new_email,'password' :'valid-password'})
    login_token2 = login_response2.json()['token']
    login_u_id2 = login_response2.json()['auth_user_id']
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token2}&u_id={login_u_id2}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == login_u_id
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == new_email
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    requests.delete(f"{BASE_URL}clear/v1")


def test_user_successfully_found_user_but_invalid_new_email():
    requests.delete(f"{BASE_URL}clear/v1")
    
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    assert register_response.status_code == 200
    
    new_invalid_email = 'new-email!@gmail.com' 
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token,"email": new_invalid_email}) 
    assert email_edit_response.status_code == 400

    new_invalid_email = 'new-email@!gmail.com' 
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token,"email": new_invalid_email}) 
    assert email_edit_response.status_code == 400

    new_invalid_email = 'new-email@gmail.!com' 
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token,"email": new_invalid_email}) 
    assert email_edit_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")


def test_user_successfully_found_user_but_taken_new_email():
    requests.delete(f"{BASE_URL}clear/v1")
    
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']
    assert register_response.status_code == 200

    new_taken_email = 'new-email@gmail.com' 
    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": new_taken_email, "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response2.status_code == 200

    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token,"email": new_taken_email}) 
    assert email_edit_response.status_code == 400

    requests.delete(f"{BASE_URL}clear/v1")


def test_multiple_users_successfully_found_and_edited_email():
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

    register_response2 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response2.status_code == 200
    register_response3 = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email3@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response3.status_code == 200

    new_email = 'new-email@gmail.com'
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token,"email": new_email}) 
    assert email_edit_response.status_code == 200

    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={register_token}&u_id={register_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == new_email
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={'email': new_email,'password' :'valid-password'})
    login_token = login_response.json()['token']
    login_u_id = login_response.json()['auth_user_id']
    get_profile_response = requests.get(f"{BASE_URL}user/profile/v1?token={login_token}&u_id={login_u_id}")
    user = get_profile_response.json()
    assert get_profile_response.status_code == 200
    assert user['user']['u_id'] == login_u_id
    assert user['user']['u_id'] == register_u_id
    assert user['user']['email'] == new_email
    assert user['user']['name_first'] == "John"
    assert user['user']['name_last'] == "Smith"

    requests.delete(f"{BASE_URL}clear/v1")


def test_token_is_wrong():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    register_token = register_response.json()['token']

    new_email = 'new-email@gmail.com'
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token + 'X',"email": new_email}) 
    assert email_edit_response.status_code == 403
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token[::-1],"email": new_email}) 
    assert email_edit_response.status_code == 403
    email_edit_response = requests.put(f"{BASE_URL}user/profile/setemail/v1", json={"token": register_token[:-1],"email": new_email}) 
    assert email_edit_response.status_code == 403

    requests.delete(f"{BASE_URL}clear/v1")
