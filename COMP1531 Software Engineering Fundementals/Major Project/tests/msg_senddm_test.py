import json
import requests
import pytest
from src.other import clear_v1
from src import config
from tests.test_msg_helper import *
from src.auth import *
from src.dm import *


INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
INVALID = -1
LEN_INVALID_MSG = 'too long whoops' * 2000
LEN_SHORT_MSG = ''

 
@pytest.fixture(autouse=True)
def clear():
   requests.delete(config.url + "clear/v1")

# AccessError when invalid token
def test_invalid_token():
    response = senddm(INVALID, 0, 'hello')
    assert response.status_code == ACCESS_ERROR

# InputError when any of dm_id does not refer to a valid DM
def test_dm_id_invalid():
    user = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    response = senddm(user['token'], INVALID, 'hi')
    assert response.status_code == INPUT_ERROR

# InputError when length of message is less than 1 char
def test_msg_senddm_msg_is_short():
    user1 = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user1['token'], [user2['auth_user_id']])

    response = senddm(user1['token'], dm.json()['dm_id'], LEN_SHORT_MSG)

    assert response.status_code == INPUT_ERROR


#InputError when length of message is over 1000 chars
def test_msg_senddm_msg_too_long():
    user1 = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user1['token'], [user2['auth_user_id']])

    response = senddm(user1['token'], dm.json()['dm_id'], LEN_INVALID_MSG)
    assert response.status_code == INPUT_ERROR

#AccessError when dm_id is valid and the user is not a member of the DM
def test_msg_senddm_user_not_in_dm():
    user = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    user3 = auth_register("email3@gmail.com", "12345673", "jinny", "seo")
    
    dm = dm_create(user['token'], [user2['auth_user_id']])

    response = senddm(user3['token'], dm.json()['dm_id'], 'hi')
    assert response.status_code == ACCESS_ERROR

# send the same msg to a DM twice
def test_msg_senddm_success_same_msg():
    user = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")

    dm = dm_create(user['token'], [user2['auth_user_id']])
    response = senddm(user['token'], dm.json()['dm_id'], 'hi')
    dm_id = response.json()['message_id']

    response2 = senddm(user2['token'], dm.json()['dm_id'], 'hi')
    dm_id2 = response2.json()['message_id']

    assert response.status_code == SUCCESS
    assert dm_id != dm_id2

#send the same msg to DM and channel seperately 
def test_msg_senddm_success_to_dm_and_channel():
    user = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    
    # send to DM
    dm = dm_create(user['token'], [user2['auth_user_id']])
    dm_id = dm.json()['dm_id']

    response = senddm(user['token'], dm_id, 'Hi')
    print(response.json())
    message_id = response.json()['message_id']

    # send to channel
    channel = channel_create(user['token'], 'name', True)
    send_message(user['token'], channel['channel_id'], 'Hi')
    message_id2 = ['message_id']
    

    assert response.status_code == SUCCESS
    assert message_id != message_id2
