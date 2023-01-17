import pytest
from src.auth import auth_login_v1, auth_register_v1
from src.other import clear_v1
from src.error import InputError

def test_unregistered_email():
    clear_v1()
    with pytest.raises(InputError):
        auth_login_v1('unregistered-email@gmail.com', 'xxxxxx')
    clear_v1()

def test_invalid_email_format():
    clear_v1()
    with pytest.raises(InputError):
        auth_login_v1('non-existant-email@gmail', 'xxxxxx')
        auth_login_v1('non-existant-email', 'xxxxxx')
    clear_v1()

def test_wrong_password():
    clear_v1()
    auth_register_v1('user1@gmail.com', 'right-password', 'John', 'Smith')
    with pytest.raises(InputError):
        auth_login_v1('user1@gmail.com', 'wrong-password')
    clear_v1()

def test_correct_login_details():
    clear_v1()
    register_user_id1 = auth_register_v1('user1@gmail.com', 'right-password', 'John', 'Smith')
    assert auth_login_v1('user1@gmail.com', 'right-password') == register_user_id1
    register_user_id2 = auth_register_v1('user2@gmail.com', 'right-password', 'John', 'Smith')
    assert auth_login_v1('user2@gmail.com', 'right-password') == register_user_id2
    clear_v1()

def test_multiple_users_can_be_logged_into():
    clear_v1()
    user1_id = auth_register_v1('valid-email@gmail.com', 'valid-password', 'John', 'Smith')
    user2_id = auth_register_v1('valid-email2@gmail.com', 'valid-password', 'John', 'Smith')
    user3_id = auth_register_v1('valid-email3@gmail.com', 'valid-password', 'John', 'Smith')
    user_successful_login_arr = [
        auth_login_v1('valid-email@gmail.com', 'valid-password') == user1_id,
        auth_login_v1('valid-email2@gmail.com', 'valid-password') == user2_id,
        auth_login_v1('valid-email3@gmail.com', 'valid-password') == user3_id
    ]
    assert all(user_successful_login_arr)
    clear_v1()

def test_non_alphanumeric_names_and_passwords_registration_users_can_be_logged_into():
    clear_v1()
    user1_id = auth_register_v1('valid-email@gmail.com', 'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?', 'John', 'Smith')
    user2_id = auth_register_v1('valid-email2@gmail.com', 'valid-password', 'John`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?', 'Smith')
    user3_id = auth_register_v1('valid-email3@gmail.com', 'valid-password', 'John', 'Smith`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?')
    user4_id = auth_register_v1('valid-email4@gmail.com', 'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?', 'John`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?', 'Smith`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?')
    user_successful_login_arr = [
        auth_login_v1('valid-email@gmail.com', 'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?') == user1_id,
        auth_login_v1('valid-email2@gmail.com', 'valid-password') == user2_id,
        auth_login_v1('valid-email3@gmail.com', 'valid-password') == user3_id,
        auth_login_v1('valid-email4@gmail.com', 'valid-password`~!@#$%^&*()_+-=[]\\{}|;\'\";:,./<>?') == user4_id
    ]
    assert all(user_successful_login_arr)
    clear_v1()


def test_register_with_taken_email():
    clear_v1()
    succesful_user_id = auth_register_v1('email@gmail.com', 'valid-password', 'John', 'Smith')
    with pytest.raises(InputError):
        auth_register_v1('email@gmail.com', 'valid-password', 'John', 'Smith')
    assert auth_login_v1('email@gmail.com', 'valid-password') == succesful_user_id
    clear_v1()