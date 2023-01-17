import pytest
from src.other import clear_v1
from src.config import url

import requests
import jwt

BASE_URL = url
SECRET = "COMP1531"

def test_multiple_registers_create_unique_IDs():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user1_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user2_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email3@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user3_id = register_response_data["auth_user_id"]
    user_id_arr = [
        user1_id,
        user2_id,
        user3_id
    ]
    unique_ids = (len(user_id_arr) == len(set(user_id_arr)))
    assert unique_ids
    requests.delete(f"{BASE_URL}clear/v1")

def test_non_alphanumeric_names_and_passwords_registration():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email@gmail.com", "password": "valid-password`~!@#$%^&*()_+-=[]\\{}|;\"\";:,./<>?","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user1_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email2@gmail.com", "password": "valid-password","name_first": "John`~!@#$%^&*()_+-=[]\\{}|;\"\";:,./<>?","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user2_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email3@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith`~!@#$%^&*()_+-=[]\\{}|;\"\";:,./<>?"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user3_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "valid-email4@gmail.com", "password": "valid-password`~!@#$%^&*()_+-=[]\\{}|;\"\";:,./<>?","name_first": "John`~!@#$%^&*()_+-=[]\\{}|;\"\";:,./<>?","name_last": "Smith`~!@#$%^&*()_+-=[]\\{}|;\"\";:,./<>?"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user4_id = register_response_data["auth_user_id"]

    user_id_arr = [
        user1_id,
        user2_id,
        user3_id,
        user4_id,
    ]
    unique_ids = (len(user_id_arr) == len(set(user_id_arr)))
    assert unique_ids
    requests.delete(f"{BASE_URL}clear/v1")

def test_register_with_taken_password_and_or_name():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email1@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user1_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email2@gmail.com", "password": "valid-password","name_first": "John2","name_last": "Smith2"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user2_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email3@gmail.com", "password": "valid-password1","name_first": "John","name_last": "Smith3"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user3_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email4@gmail.com", "password": "valid-password2","name_first": "John3","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user4_id = register_response_data["auth_user_id"]
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email5@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    register_response_data = register_response.json()
    user5_id = register_response_data["auth_user_id"]

    user_id_arr = [
        user1_id,
        user2_id,
        user3_id,
        user4_id,
        user5_id,
    ]
    unique_ids = (len(user_id_arr) == len(set(user_id_arr)))
    assert unique_ids
    requests.delete(f"{BASE_URL}clear/v1")

def test_invalid_name_length():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "xxxxxx","name_first": "","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "xxxxxx","name_first": "John","name_last": ""})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "xxxxxx","name_first": "","name_last": ""})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "xxxxxx","name_first": "Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "xxxxxx","name_first": "John","name_last": "Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "xxxxxx","name_first": "Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx","name_last": "Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"})
    assert register_response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")

def test_less_than_6_character_password():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "xxxxx","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "x","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")

def test_invalid_email_format():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@gmail", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email!@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email#@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email$@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email^@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email&@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email*@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email(@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email)@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email=@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email[@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email]@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email{@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email}@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email;@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email:@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email\"@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email\"@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email,@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email<@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email>@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email/@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email?@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email\\@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email|@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@!gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@#gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@$gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@%gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@^gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@&gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@*gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@(gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@)gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@_gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@=gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@+gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@[gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@]gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@{gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@}gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@\\gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@|gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@;gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@:gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@\"gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@\"gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@,gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@<gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@>gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@/gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@?gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "invalid-email@gmail.com1", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")

def test_register_with_taken_email():
    requests.delete(f"{BASE_URL}clear/v1")
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "valid-password","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 200
    succesful_user_id = register_response.json()
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email@gmail.com", "password": "valid-password2","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email@gmail.com","password" :"valid-password2"})
    assert login_response.status_code == 400
    login_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email@gmail.com","password" :"valid-password"})
    response_data = login_response.json()
    assert response_data["auth_user_id"] == succesful_user_id["auth_user_id"]
    assert login_response.status_code == 200
    requests.delete(f"{BASE_URL}clear/v1")

def test_invalid_registration_not_added_to_data_store():
    requests.delete(f"{BASE_URL}clear/v1")
    requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email11@gmail.com", "password": "valid-password1","name_first": "John","name_last": "Smith"})
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email1@gmail.com", "password": "xxxxxx","name_first": "","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email2@gmail.com", "password": "xxxxxx","name_first": "John","name_last": ""})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email3@gmail.com", "password": "xxxxxx","name_first": "","name_last": ""})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email4@gmail.com", "password": "xxxxxx","name_first": "Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email5@gmail.com", "password": "xxxxxx","name_first": "John","name_last": "Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email6@gmail.com", "password": "xxxxxx","name_first": "Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx","name_last": "Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email7@gmail.com", "password": "xxxxx","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email8@gmail.com", "password": "x","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email9@gmail.com", "password": "","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email10@gmail.com", "password": "x","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/register/v2", json={"email": "email11@gmail.com", "password": "valid-password2","name_first": "John","name_last": "Smith"})
    assert register_response.status_code == 400

    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email1@gmail.com", "password": "xxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email2@gmail.com", "password": "xxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email3@gmail.com", "password": "xxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email4@gmail.com", "password": "xxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email5@gmail.com", "password": "xxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email6@gmail.com", "password": "xxxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email7@gmail.com", "password": "xxxxx"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email8@gmail.com", "password": "x"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email9@gmail.com", "password": ""})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email10@gmail.com", "password": "x"})
    assert register_response.status_code == 400
    register_response = requests.post(f"{BASE_URL}auth/login/v2", json={"email": "email11@gmail.com", "password": "valid-password2"})
    assert register_response.status_code == 400
    requests.delete(f"{BASE_URL}clear/v1")
