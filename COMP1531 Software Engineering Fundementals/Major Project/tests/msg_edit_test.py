import json
import requests
import pytest
from src.other import clear_v1
from src import config
from tests.test_msg_helper import *
from src.auth import *
from src.security_helper import *

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
INVALID = -1
LEN_INVALID_MSG = 'too long whoops' * 2000
EMPTY_MSG = ''
 
@pytest.fixture(autouse=True)
def clear():
   requests.delete(config.url + "clear/v1")

#input error when token is invalid
def test_token_invalid():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    channel_id = channel_create(user['token'], 'name', True)
    msg_response = requests.post(f'{config.url}/message/send/v1', json={
        'token': user['token'],
        'channel_id': channel_id['channel_id'],
        'message': "hello"
    })

    edit_resp = requests.put(f'{config.url}/message/edit/v1', json={
        'token': "invalid",
        'message_id': msg_response.json()['message_id'],
        'message': 'failed'
    })

    assert edit_resp.status_code == ACCESS_ERROR

# Test for InputError when message_id does not refer to a valid message
def test_edit_input_error():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel = channel_create(user['token'], "my channel", True)
    # Send a Message in created channel
    send_message(user['token'], channel['channel_id'], 'hi!')
    edit_response = requests.put(f'{config.url}/message/edit/v1', json={
        'token': user['token'], 
        'message_id': INVALID, 
        'message': 'hi'
    })
    assert edit_response.status_code == INPUT_ERROR


#input error when more than 1k char
def test_more_than_1000_msg():
    user = auth_register('email123@gmail.com', 'password', 'firstname', 'lastname')
    channel_id = channel_create(user['token'], "channel", True)
    send_message(user['token'], channel_id['channel_id'], 'hi!')
    response = requests.put(f'{config.url}message/edit/v1', json={
       "token": user['token'],
       'message_id': 0,
       "message": LEN_INVALID_MSG,
    })
    assert response.status_code == INPUT_ERROR

#access error when no permission 
def test_edit_access_error_no_permissions():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    user2 = auth_register('email2@gmail.com', 'password2', 'firstname2', 'lastname2')
    channel_id = channel_create(user['token'], "channel", True)
    send_message(user['token'], channel_id['channel_id'], 'hi!')
    edit_response = requests.put(f'{config.url}/message/edit/v1', json={
        'token': user2['token'], 
        'message_id': 0, 
        'message': 'hi'
    })
    assert edit_response.status_code == ACCESS_ERROR

#access error when no permission 
def test_edit_access_error_no_dm_permissions():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    user2 = auth_register('email2@gmail.com', 'password2', 'firstname2', 'lastname2')
    user3 = auth_register('email3@gmail.com', 'password3', 'firstname3', 'lastname3')
    dm_id = dm_create(user['token'], [user2['auth_user_id']])
    senddm(user['token'], dm_id.json()['dm_id'], 'HI')
    edit_response = requests.put(f'{config.url}/message/edit/v1', json={
        'token': user3['token'], 
        'message_id': 0, 
        'message': 'hisdfgsdgsdfg'
    })
    assert edit_response.status_code == ACCESS_ERROR

#test when new message is an empty string, the message is deleted.
def test_empty_string_remove():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel_id = channel_create(user['token'], "channel", True)
    send_message(user['token'], channel_id['channel_id'], 'hi')
    edit_response = requests.put(f'{config.url}/message/edit/v1', json={
        'token': user['token'], 
        'message_id': 0, 
        'message': EMPTY_MSG
    })
    assert edit_response.status_code == SUCCESS


#test functionality
def test_functionality():
    user = auth_register('email@gmail.com', 'password', 'firstname', 'lastname')
    channel = channel_create(user['token'], "channel", True)
    send_message(user['token'], channel['channel_id'], 'hi!')
    message = send_message(
        user['token'], channel['channel_id'], 'message2')
    response = requests.put(f'{config.url}message/edit/v1', json={
        'token': user['token'],
        'message_id': message['message_id'],
        'message': "hi",
    })
    assert response.status_code == SUCCESS

