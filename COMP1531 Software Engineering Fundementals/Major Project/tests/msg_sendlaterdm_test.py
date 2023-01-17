import json
import requests
import pytest
from src.other import clear_v1
from src import config
from tests.test_msg_helper import *
from src.auth import *
from src.dm import *
import datetime


INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
INVALID = -1
LEN_INVALID_MSG = 'too long whoops' * 2000
LEN_SHORT_MSG = ''
CURR_TIME = int(datetime.datetime.now().timestamp())

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

# AccessError when invalid token
def test_invalid_token():
    user1 = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user1['token'], [user2['auth_user_id']])

    message_response = requests.post(f'{config.url}message/sendlaterdm/v1', json={
        'token': INVALID,
        'dm_id': dm.json()['dm_id'],
        'message': "message",
        'time_sent': CURR_TIME + 1
    })
    assert message_response.status_code == ACCESS_ERROR

def test_dm_id_invalid():
    user = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    message_response = requests.post(f'{config.url}message/sendlaterdm/v1', json={
        'token': user['token'],
        'dm_id': INVALID,
        'message': "message",
        'time_sent': CURR_TIME + 1
    })
    assert message_response.status_code == INPUT_ERROR

def test_msg_senddm_msg_is_short():
    user1 = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user1['token'], [user2['auth_user_id']])

    message_response = requests.post(f'{config.url}message/sendlaterdm/v1', json={
        'token': user1['token'],
        'dm_id': dm.json()['dm_id'],
        'message': LEN_SHORT_MSG,
        'time_sent': CURR_TIME + 1
    })

    assert message_response.status_code == INPUT_ERROR

#InputError when length of message is over 1000 chars
def test_msg_senddm_msg_too_long():
    user1 = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user1['token'], [user2['auth_user_id']])

    message_response = requests.post(f'{config.url}message/sendlaterdm/v1', json={
        'token': user1['token'],
        'dm_id': dm.json()['dm_id'],
        'message': LEN_INVALID_MSG,
        'time_sent': CURR_TIME + 1
    })
    assert message_response.status_code == INPUT_ERROR

def test_msg_sent_in_past():
    user1 = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user1['token'], [user2['auth_user_id']])

    message_response = requests.post(f'{config.url}message/sendlaterdm/v1', json={
        'token': user1['token'],
        'dm_id': dm.json()['dm_id'],
        'message': 'message',
        'time_sent': CURR_TIME - 1
    })
    assert message_response.status_code == INPUT_ERROR

#AccessError when dm_id is valid and the user is not a member of the DM
def test_msg_senddm_user_not_in_dm():
    user = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    user3 = auth_register("email3@gmail.com", "12345673", "jinny", "seo")
    
    dm = dm_create(user['token'], [user2['auth_user_id']])

    message_response = requests.post(f'{config.url}message/sendlaterdm/v1', json={
        'token': user3['token'],
        'dm_id': dm.json()['dm_id'],
        'message': 'message',
        'time_sent': CURR_TIME + 1
    })
    assert message_response.status_code == ACCESS_ERROR

