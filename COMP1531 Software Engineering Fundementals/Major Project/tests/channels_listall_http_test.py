import json
import requests
import pytest
from src import config

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
CHANNEL_IS_PUBLIC = True
CHANNEL_IS_PRIVATE = False

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "/clear/v1")


def test_http_listall_one_channel():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel_public', 'is_public': CHANNEL_IS_PUBLIC})
    listall_http = requests.get(config.url + "/channels/listall/v2", params={'token': token})
    assert(listall_http.json() == {'channels': [{'channel_id': 1, 'name': 'channel_public'}]})

def test_http_listall_multiple_public():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    channel1_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel_public', 'is_public': CHANNEL_IS_PUBLIC})
    channel1_id = channel1_http.json()['channel_id']
    channel2_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel_public1', 'is_public': CHANNEL_IS_PUBLIC})
    channel2_id = channel2_http.json()['channel_id']
    listall_http = requests.get(config.url + "/channels/listall/v2", params={'token': token})
    assert(listall_http.json() == ({'channels':[{'channel_id': channel1_id , 'name': 'channel_public'}, {'channel_id': channel2_id, 'name': 'channel_public1'}]}))

def test_http_listall_no_channels():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    listall_http = requests.get(config.url + "/channels/listall/v2", params={'token': token})
    assert(listall_http.json() == {'channels': []})

def test_http_listall_public_private():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    #Create Public Channel
    channel1_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel_public', 'is_public': CHANNEL_IS_PUBLIC})
    channel1_id = channel1_http.json()['channel_id']
    # Create Private Channel
    channel2_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel_private', 'is_public': CHANNEL_IS_PRIVATE})
    channel2_id = channel2_http.json()['channel_id']
    listall_http = requests.get(config.url + "/channels/listall/v2", params={'token': token})
    assert(listall_http.json() == ({'channels': [{'channel_id': channel1_id, 'name': 'channel_public'}, {'channel_id': channel2_id, 'name': 'channel_private'}]}))

def test_http_listall_access_error():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': 'channel_public', 'is_public': CHANNEL_IS_PUBLIC})
    listall_http = requests.get(config.url + "/channels/listall/v2", params={'token': 123123123})
    assert(listall_http.status_code == ACCESS_ERROR)
