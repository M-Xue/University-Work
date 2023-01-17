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
    print(user)
    channel_id = channel_create(user['token'], 'name', True)
    channels_response = channel_message(INVALID, channel_id['channel_id'], 0)
    assert channels_response.status_code == ACCESS_ERROR

#inputerror when is not a valid channel
def test_channel_invalid():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    channel_create(user['token'], 'name', True)
    channels_response = channel_message(user['token'], INVALID, 0)
    assert channels_response.status_code == INPUT_ERROR

#access error when channel_id is valid and the authorised user is not a member of the channel
def test_mem_invalid():
    user1 = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    channel_id = channel_create(user1['token'], 'name', True)
    user2 = auth_register('email22@gmail.com', '1234567', 'nope', 'no')
    channels_response = channel_message(user2['token'], channel_id['channel_id'], 0)
    assert channels_response.status_code == ACCESS_ERROR

#test for input error when start is greater than the total number of messages in the channel
def test_start_greater_than_total():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    channel_id = channel_create(user['token'], 'name', True)
    channels_response = channel_message(user['token'], channel_id['channel_id'], 1)
    assert channels_response.status_code == INPUT_ERROR


#test normal functionality 
def test_success_return_end():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    channel_id = channel_create(user['token'], 'name', True)

    #send 100 messages
    for text in range(100):
        send_message(user['token'], channel_id['channel_id'], 'HI' + str(text + 1))

    #return from 90th 
    response = requests.get(f'{config.url}/channel/messages/v2', params={
        'token': user['token'],
        'channel_id': channel_id['channel_id'],
        'start': 90
        })
    message = response.json()


    assert len(message['messages']) == 10
    assert message['start'] == 90
    assert message['end'] == -1
    


def test_less_than_50():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    channel_id = channel_create(user['token'], 'name', True)

    #send 10 messages
    for text in range(10):
        send_message(user['token'], channel_id['channel_id'], 'message' + str(text + 1))

    #return from 5th 
    response = requests.get(f'{config.url}/channel/messages/v2', params={
        'token': user['token'],
        'channel_id': channel_id['channel_id'],
        'start': 5
        })
    msg = response.json()

    assert len(msg['messages']) == 5
    assert msg['start'] == 5
    assert msg['end'] == -1

def test_more_than_50():
    user = auth_register('email@gmail.com', '1234567', 'jinny', 'seo')
    channel_id = channel_create(user['token'], 'name', True)

    #send 100 messages
    for text in range(100):
        send_message(user['token'], channel_id['channel_id'], 'message' + str(text + 1))

    #return from 50th 
    response = requests.get(f'{config.url}/channel/messages/v2', params={
        'token': user['token'],
        'channel_id': channel_id['channel_id'],
        'start': 50
        })
    msg = response.json()

    assert len(msg['messages']) == 50
    assert msg['start'] == 50
    assert msg['end'] == 100


