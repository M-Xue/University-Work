import json
import requests
import pytest
from src import config

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200
CHANNEL_NAME = "Name"
CHANNEL_IS_PUBLIC = True
NONEXISTANT_CHANNEL_ID = 1000000

owners_dict = [
    {
        'u_id': 0,
        'email': 'example1@gmail.com',
        'name_first': "John",
        'name_last': "Smith",
        'handle_str': "johnsmith",
    }
]

members_dict = [
    {
        'u_id': 0,
        'email': 'example1@gmail.com',
        'name_first': "John",
        'name_last': "Smith",
        'handle_str': "johnsmith",
    },

    {
        'u_id': 1,
        'email': 'example2@gmail.com',
        'name_first': "Dave",
        'name_last': "Cave",
        'handle_str': "davecave",
    },

    {
        'u_id': 2,
        'email': 'example3@gmail.com',
        'name_first': "Steve",
        'name_last': "Mine",
        'handle_str': "stevemine",
    }
]
@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "/clear/v1")

def test_http_details_owner():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': token, 'channel_id': channel_id})
    assert details_http.status_code == SUCCESS
    assert(details_http.json() == {'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC, 'owner_members': owners_dict, 'all_members': owners_dict})


def test_http_multiple_members():
    # Register 3 users
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']

    token3_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example3@gmail.com',
        'password': 'password',
        'name_first': 'Steve',
        'name_last': 'Mine'})
    token3 = token3_http.json()['token']

    # Create Channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Let user 2 and 3 join channel
    requests.post(config.url + "/channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    requests.post(config.url + "/channel/join/v2", json={'token': token3, 'channel_id': channel_id})

    # Send post req
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': token1, 'channel_id': channel_id})
    assert details_http.status_code == SUCCESS
    assert(details_http.json() == {'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC, 'owner_members': owners_dict, 'all_members': members_dict})


def test_http_details_access_error_invalid_channel():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    details_req = requests.get(config.url + "/channel/details/v2", params={'token': token, 'channel_id': NONEXISTANT_CHANNEL_ID})
    assert(details_req.status_code == INPUT_ERROR)


def test_http_details_access_error_unauthorised():
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']

    # Create channel with user 1
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token1, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']

    # Call for channel details from user 2
    details_req = requests.get(config.url + "/channel/details/v2", params={'token': token2, 'channel_id': channel_id})
    assert(details_req.status_code == ACCESS_ERROR)

def test_http_details_access_error_invalid_token():
    token_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token = token_http.json()['token']
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': 123123123, 'channel_id': channel_id})
    assert(details_http.status_code == ACCESS_ERROR)

def test_http_details_global_owner_access():
    # First user is Global owner
    token1_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example1@gmail.com',
        'password': 'password',
        'name_first': 'John',
        'name_last': 'Smith'})
    token1 = token1_http.json()['token']

    token2_http = requests.post(config.url + "/auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave'})
    token2 = token2_http.json()['token']

    # User 2  creates the channel
    channel_http = requests.post(config.url + "/channels/create/v2", json={'token': token2, 'name': CHANNEL_NAME, 'is_public': CHANNEL_IS_PUBLIC})
    channel_id = channel_http.json()['channel_id']
    # User 1 (global owner) calls for channel details
    details_http = requests.get(config.url + "/channel/details/v2", params={'token': token1, 'channel_id': channel_id})
    assert(details_http.status_code == SUCCESS)
    assert(details_http.json() == ({
        'name': CHANNEL_NAME, 
        'is_public': CHANNEL_IS_PUBLIC, 
        'owner_members': [{ 'u_id': 1, 'email': 'example2@gmail.com', 'name_first': "Dave", 'name_last': "Cave", 'handle_str': "davecave",}], 
        'all_members': [{ 'u_id': 1, 'email': 'example2@gmail.com', 'name_first': "Dave", 'name_last': "Cave", 'handle_str': "davecave",}]
        }))


