import json
import requests
import pytest
from src.other import clear_v1
from src import config
INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

# test valid list of both public and private channels
def test_channels_list_valid_public_private():
    user_1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token_1 = user_1.json()['token']

    user_2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token_2 = user_2.json()['token']

    channel_1 = requests.post(config.url + 'channels/create/v2', json={
        'token': token_1,
        'name': "channel_1_public",
        "is_public": True})

    channel_1_id = channel_1.json()['channel_id']

    channel_2 = requests.post(config.url + 'channels/create/v2', json={
        'token': token_2,
        'name': "channel_2_private",
        "is_public": False})

    channel_2_id = channel_2.json()['channel_id']

    channel_list = requests.get(config.url + 'channels/list/v2', params={
        'token': token_1})

    assert channel_list.status_code == SUCCESS
    assert channel_list.json() == {'channels': [{'channel_id' : channel_1_id, 'name': 'channel_1_public'}]}
    channel_list_2 = requests.get(config.url + 'channels/list/v2', params={
        'token': token_2})
    assert channel_list_2.status_code == SUCCESS
    assert channel_list_2.json() == {'channels': [{'channel_id' : channel_2_id, 'name': 'channel_2_private'}]}

# test empty list
def test_channels_list_empty():
    list_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = list_response.json()['token']
    response = requests.get(config.url + 'channels/list/v2', params={
        'token': token})
    assert response.status_code == SUCCESS
    assert response.json() == {'channels':[]}

# given invalid token
def test_list_invalid_token():
    token = "-1"
    response = requests.get(config.url + 'channels/list/v2', params={
        'token': token})
    assert response.status_code == ACCESS_ERROR

# test valid list of PUBLIC channels
def test_channels_list_valid_all_public():
    list_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token_1 = list_response.json()['token']

    response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token_2 = response.json()['token']

    channel_1 = requests.post(config.url + 'channels/create/v2', json={
        'token': token_1,
        'name': "channel_1_public",
        "is_public": True})

    channel_1_id = channel_1.json()['channel_id']

    channel_2 = requests.post(config.url + 'channels/create/v2', json={
        'token': token_2,
        'name': "channel_2_public",
        "is_public": True})

    channel_2_id = channel_2.json()['channel_id']

    channel_list = requests.get(config.url + 'channels/list/v2', params={
        'token': token_1})

    assert channel_list.status_code == SUCCESS
    assert channel_list.json() == {'channels': [{'channel_id' : channel_1_id, 'name': 'channel_1_public'}]}

    channel_list_2 = requests.get(config.url + 'channels/list/v2', params={
        'token': token_2})

    assert channel_list_2.status_code == SUCCESS
    assert channel_list_2.json() == {'channels': [{'channel_id' : channel_2_id, 'name': 'channel_2_public'}]}


# test valid list of PRIVATE channels
def test_channels_list_valid_all_private():
    list_response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token_1 = list_response.json()['token']

    response = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token_2 = response.json()['token']

    channel_1 = requests.post(config.url + 'channels/create/v2', json={
        'token': token_1,
        'name': "channel_1_private",
        "is_public": False})

    channel_1_id = channel_1.json()['channel_id']

    channel_2 = requests.post(config.url + 'channels/create/v2', json={
        'token': token_2,
        'name': "channel_2_private",
        "is_public": False})

    channel_2_id = channel_2.json()['channel_id']

    channel_list = requests.get(config.url + 'channels/list/v2', params={
        'token': token_1})

    assert channel_list.status_code == SUCCESS
    assert channel_list.json() == {'channels': [{'channel_id' : channel_1_id, 'name': 'channel_1_private'}]}

    channel_list_2 = requests.get(config.url + 'channels/list/v2', params={
        'token': token_2})

    assert channel_list_2.status_code == SUCCESS
    assert channel_list_2.json() == {'channels': [{'channel_id' : channel_2_id, 'name': 'channel_2_private'}]}
