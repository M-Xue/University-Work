import json
import requests
import pytest
from src.other import clear_v1
from src import config
from tests.test_msg_helper import *
from src.auth import *
from src.channel import *
from src.channels import *
from src.message import *
from src.dm import *
 
INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
INVALID = -1
 
@pytest.fixture(autouse=True)
def clear():
   requests.delete(config.url + "clear/v1")

#access error when token is invalid
def test_token_invalid():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    user2 = auth_register('email2@gmail.com', '12345672', 'jinny', 'seo')
    dm = dm_create(user['token'], [user2['auth_user_id']])
    dm_response = dm_messages(INVALID, dm, 0)
    assert dm_response.status_code == ACCESS_ERROR

#inputerror when is not a valid dm
def test_dm_invalid():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    dm_response = dm_messages(user['token'], INVALID, 0)

    assert dm_response.status_code == INPUT_ERROR

#access error when channel_id is valid and the authorised user is not a member of the channel
def test_mem_invalid():
    user = auth_register("email@gmail.com", "1234567", "jinny", "seo")
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    user3 = auth_register("email3@gmail.com", "12345673", "jinny", "seo")
    
    dm = dm_create(user['token'], [user2['auth_user_id']])

    response = dm_messages(user3['token'], dm.json()['dm_id'], 0)
    assert response.status_code == ACCESS_ERROR

#test for input error when start is greater than the total number of messages in the channel
def test_start_greater_than_total():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user['token'], [user2['auth_user_id']])
    dm_response = dm_messages(user['token'], dm.json()['dm_id'], 1)
    assert dm_response.status_code == INPUT_ERROR


#test normal functionality 
def test_success_return_end():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user['token'], [user2['auth_user_id']])

    #send 100 messages
    for text in range(100):
        senddm(user['token'], dm.json()['dm_id'], 'HI' + str(text + 1))

    #return from 90th 
    response = requests.get(f'{config.url}/dm/messages/v1', params={
        'token': user['token'],
        'dm_id': dm.json()['dm_id'],
        'start': 90
        })

    message = response.json()


    assert len(message['messages']) == 10
    assert message['start'] == 90
    assert message['end'] == -1
    


def test_less_than_50():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user['token'], [user2['auth_user_id']])

    #send 10 messages
    for text in range(10):
        senddm(user['token'], dm.json()['dm_id'], 'message' + str(text + 1))

    #return from 5th 
    response = requests.get(f'{config.url}/dm/messages/v1', params={
        'token': user['token'],
        'dm_id': dm.json()['dm_id'],
        'start': 5
        })
    message = response.json()


    assert len(message['messages']) == 5
    assert message['start'] == 5
    assert message['end'] == -1

def test_more_than_50():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    user2 = auth_register("email2@gmail.com", "12345672", "jinny", "seo")
    dm = dm_create(user['token'], [user2['auth_user_id']])

    #send 10 messages
    for text in range(100):
        senddm(user['token'], dm.json()['dm_id'], 'message' + str(text + 1))

    #return from 5th 
    response = requests.get(f'{config.url}/dm/messages/v1', params={
        'token': user['token'],
        'dm_id': dm.json()['dm_id'],
        'start': 50
        })
    message = response.json()


    assert len(message['messages']) == 50
    assert message['start'] == 50
    assert message['end'] == 100
