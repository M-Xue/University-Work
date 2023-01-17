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
########## DM_REMOVE_V1 TESTS ##########
# Input Error when dm_id does not refer to a valid DM
def test_dm_remove_invalid_dm_id():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_token = owner.json()['token']

    user_1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave',
    })
    token_1= user_1.json()['token']
    u_id1 = user_1.json()['auth_user_id']

    requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [u_id1]})

    response = requests.delete(config.url + 'dm/remove/v1', json={
            'token': token_1,
            'dm_id': 123})
    assert response.status_code == INPUT_ERROR

# AccessError when dm_id is valid and the authorised user is not the original DM creator
def test_remove_dm_user_not_creator():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_token = owner.json()['token']
    user = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_token = user.json()['token']
    user_u_id = owner.json()['auth_user_id']

    dm_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [user_u_id]})
    dm_id = dm_1.json()['dm_id']
    response = requests.delete(config.url + 'dm/remove/v1', json={
            'token': user_token,
            'dm_id': dm_id})
    assert response.status_code == ACCESS_ERROR

# AccessError when dm_id is valid and the authorised user is no longer in the DM
def test_remove_dm_authorised_user_not_in_dm():
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })

    token_1 = user_1.json()['token']

    user_2 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })

    token_2 = user_2.json()['token']
    u_id_2 = user_2.json()['auth_user_id']

    # create dm with users
    dm_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': token_1,
            'u_ids': [u_id_2]})

    dm_id = dm_1.json()['dm_id']

    # remove the dm
    remove_request = requests.delete(config.url + 'dm/remove/v1', json={
            'token': token_2,
            'dm_id': dm_id})
    assert remove_request.status_code == ACCESS_ERROR

# invalid token is given
def test_remove_dm_invalid_token():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
    })
    owner_token = owner.json()['token']
    u_id = owner.json()['auth_user_id']
    dm_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': owner_token,
            'u_ids': [u_id]})
    dm_id = dm_1.json()['dm_id']
    response = requests.delete(config.url + 'dm/remove/v1', json={
        'token': 'invalid',
        'dm_id': dm_id})
    assert response.status_code == ACCESS_ERROR

# original creator of the DM removes the DM - all members are no longer in the DM
def test_remove_dm_valid():
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_1 = owner.json()['token']

    user_1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'example2@gmail.com',
        'password': 'password',
        'name_first': 'Dave',
        'name_last': 'Cave',
    })
    token_1 = user_1.json()['token']
    u_id1 = user_1.json()['auth_user_id']

    dm_1 = requests.post(config.url + 'dm/create/v1', json={
        'token': owner_1,
        'u_ids': [u_id1]})
    dm_id = dm_1.json()['dm_id']

    # Check if owner is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': owner_1})
    dms = dm_list_http.json()
    assert(dms == {'dms':[{'dm_id': dm_id, 'name': 'davecave, johnsmith'}]})

    # Check if user 1 is in dm
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': token_1})
    dms = dm_list_http.json()
    assert(dms == {'dms':[{'dm_id': dm_id, 'name': 'davecave, johnsmith'}]})

    # remove dm
    response = requests.delete(config.url + 'dm/remove/v1', json={
        'token': owner_1,
        'dm_id': dm_id})
    assert response.status_code == SUCCESS

    # check if owner is still in dm via dm list
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': owner_1})
    dms = dm_list_http.json()
    assert(dms == {'dms':[]} )

     # check if user 1 is still in dm via dm list
    dm_list_http = requests.get(config.url + "dm/list/v1", params={'token': token_1})
    dms = dm_list_http.json()
    assert(dms == {'dms':[]} )

