import pytest
from src.auth import auth_login_v1, auth_register_v1
from src.other import clear_v1
from src.error import InputError


def test_multiple_registers_create_unique_IDs():
    clear_v1()
    user_id_arr = [
        auth_register_v1('valid-email@gmail.com', 'valid-password', 'John', 'Smith')['auth_user_id'],
        auth_register_v1('valid-email2@gmail.com', 'valid-password', 'John', 'Smith')['auth_user_id'],
        auth_register_v1('valid-email3@gmail.com', 'valid-password', 'John', 'Smith')['auth_user_id'],
    ]
    unique_ids = (len(user_id_arr) == len(set(user_id_arr)))
    assert unique_ids
    clear_v1()

def test_non_alphanumeric_names_and_passwords_registration():
    clear_v1()
    user_id_arr = [
        auth_register_v1('valid-email@gmail.com', 'valid-password`~!@#$%^&*()_+-=[]\\{|};\'\";:,./<>?', 'John', 'Smith')['auth_user_id'],
        auth_register_v1('valid-email2@gmail.com', 'valid-password', 'John`~!@#$%^&*()_+-=[]\\{|};\'\";:,./<>?', 'Smith')['auth_user_id'],
        auth_register_v1('valid-email3@gmail.com', 'valid-password', 'John', 'Smith`~!@#$%^&*()_+-=[]\\{|};\'\";:,./<>?')['auth_user_id'],
        auth_register_v1('valid-email4@gmail.com', 'valid-password`~!@#$%^&*()_+-=[]\\{|};\'\";:,./<>?', 'John`~!@#$%^&*()_+-=[]\\{|};\'\";:,./<>?', 'Smith`~!@#$%^&*()_+-=[]\\{|};\'\";:,./<>?')['auth_user_id']
    ]
    unique_ids = (len(user_id_arr) == len(set(user_id_arr)))
    assert unique_ids
    clear_v1()

def test_register_with_taken_password_and_or_name():
    clear_v1()
    user_id_arr = [
        auth_register_v1('email1@gmail.com', 'valid-password', 'John', 'Smith')['auth_user_id'],
        auth_register_v1('email2@gmail.com', 'valid-password', 'John2', 'Smith2')['auth_user_id'],
        auth_register_v1('email3@gmail.com', 'valid-password1', 'John', 'Smith3')['auth_user_id'],
        auth_register_v1('email4@gmail.com', 'valid-password2', 'John3', 'Smith')['auth_user_id'],
        auth_register_v1('email5@gmail.com', 'valid-password', 'John', 'Smith')['auth_user_id']
    ]
    unique_ids = (len(user_id_arr) == len(set(user_id_arr)))
    assert unique_ids
    clear_v1()

def test_invalid_name_length():
    clear_v1()
    with pytest.raises(InputError):
        auth_register_v1('email1@gmail.com', 'xxxxxx', '', 'Smith')
        auth_register_v1('email2@gmail.com', 'xxxxxx', 'John', '')
        auth_register_v1('email3@gmail.com', 'xxxxxx', '', '')
        auth_register_v1('email4@gmail.com', 'xxxxxx', 'Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'Smith')
        auth_register_v1('email5@gmail.com', 'xxxxxx', 'John', 'Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx')
        auth_register_v1('email6@gmail.com', 'xxxxxx', 'Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx')
    clear_v1()

def test_less_than_6_character_password():
    clear_v1()
    with pytest.raises(InputError):
        auth_register_v1('email1@gmail.com', 'xxxxx', 'John', 'Smith')
        auth_register_v1('email2@gmail.com', 'x', 'John', 'Smith')
        auth_register_v1('email3@gmail.com', '', 'John', 'Smith')
    clear_v1()

def test_invalid_email_format():
    clear_v1()
    with pytest.raises(InputError):
        auth_register_v1('invalid-email.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@gmail', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@gmail', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email!@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email#@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email$@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email^@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email&@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email*@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email(@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email)@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email=@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email[@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email]@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email{@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email}@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email;@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email:@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email\'@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email\"@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email,@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email<@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email>@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email/@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email?@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email\\@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email|@gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@!gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@#gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@$gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@%gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@^gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@&gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@*gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@(gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@)gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@_gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@=gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@+gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@[gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@]gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@{gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@}gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@\\gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@|gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@;gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@:gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@\'gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@\"gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@,gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@<gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@>gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@/gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@?gmail.com', 'valid-password', 'John', 'Smith')
        auth_register_v1('invalid-email@gmail.com1', 'valid-password', 'John', 'Smith')
    clear_v1()


def test_register_with_taken_email():
    clear_v1()
    auth_register_v1('email@gmail.com', 'valid-password', 'John', 'Smith')
    with pytest.raises(InputError):
        auth_register_v1('email@gmail.com', 'valid-password2', 'John', 'Smith')
    with pytest.raises(InputError):
        auth_login_v1('email@gmail.com', 'valid-password2')
    clear_v1()

def test_invalid_registration_not_added_to_data_store():
    clear_v1()
    success_user_id = auth_register_v1('email@gmail.com', 'valid-password', 'John', 'Smith')['auth_user_id'] # The only valid registration that should be saved into the data store
    with pytest.raises(InputError):
        auth_register_v1('email1@gmail.com', 'xxxxxx', '', 'Smith')['auth_user_id']
        auth_register_v1('email2@gmail.com', 'xxxxxx', 'John', '')
        auth_register_v1('email3@gmail.com', 'xxxxxx', '', '')
        auth_register_v1('email4@gmail.com', 'xxxxxx', 'Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'Smith')
        auth_register_v1('email5@gmail.com', 'xxxxxx', 'John', 'Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx')
        auth_register_v1('email6@gmail.com', 'xxxxxx', 'Johnxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx', 'Smithxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx')
        auth_register_v1('email7@gmail.com', 'xxxxx', 'John', 'Smith')
        auth_register_v1('email8@gmail.com', 'x', 'John', 'Smith')
        auth_register_v1('email9@gmail.com', '', 'John', 'Smith')
        auth_register_v1('email10@gmail.com', 'x', 'John', 'Smith')
        auth_register_v1('email11@gmail.com', 'valid-password2', 'John', 'Smith')
    assert auth_login_v1('email@gmail.com', 'valid-password')['auth_user_id'] == success_user_id
    with pytest.raises(InputError):
        auth_login_v1('email1@gmail.com', 'xxxxxx')
        auth_login_v1('email2@gmail.com', 'xxxxxx')
        auth_login_v1('email3@gmail.com', 'xxxxxx')
        auth_login_v1('email4@gmail.com', 'xxxxxx')
        auth_login_v1('email5@gmail.com', 'xxxxxx')
        auth_login_v1('email6@gmail.com', 'xxxxxx')
        auth_login_v1('email7@gmail.com', 'xxxxx')
        auth_login_v1('email8@gmail.com', 'x')
        auth_login_v1('email9@gmail.com', '')
        auth_login_v1('email10@gmail.com', 'x')
        auth_login_v1('email11@gmail.com', 'valid-password2')
    clear_v1()