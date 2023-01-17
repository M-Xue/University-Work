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
########## DM_LIST_V1 TESTS ##########

# test for invalid token given
def test_dm_list_invalid_token():
    '''
    Test when token given is invalid
    '''
    response = requests.get(config.url + 'dm/list/v1', params={'token': '123456'})
    assert response.status_code == ACCESS_ERROR

# test dm list for just owner
def test_dm_list_valid():
    '''
    Test the list of one dm for owner
    '''
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })

    owner_token = owner.json()['token']
    response = requests.post(config.url + 'dm/create/v1', json={
        'token' : owner_token,
        'u_ids' : []})
    response_1 = requests.get(config.url + 'dm/list/v1', params={'token': owner_token})
    assert response_1.json()['dms'] == [{'dm_id' : 0,
              'name': 'johnsmith'
            }]
    assert response.status_code == SUCCESS

# test dm list for 1 user and 1 owner
def test_dm_list_1_user():
    '''
    Test the list of one dm for 1 user and 1 owner
    '''
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_id = owner.json()['token']

    user = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_id = user.json()['auth_user_id']

    response = requests.post(config.url + 'dm/create/v1', json={
        'token' : owner_id,
        'u_ids' : [user_id]
        })
    response_1 = requests.get(config.url + 'dm/list/v1', params={'token': owner_id})
    dm = response_1.json()['dms']
    assert dm == [{'dm_id': 0, 'name': 'johnsmith, johnsmith0'}]
    assert response.status_code == SUCCESS

# test dm list for multiple users
def test_dm_list_multiple_users():
    '''
    Test the list of dms of many users
    '''
    owner = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    owner_id = owner.json()['token']

    user = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_id = user.json()['auth_user_id']

    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email3@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_id_1 = user_1.json()['auth_user_id']

    response = requests.post(config.url + 'dm/create/v1', json={
        'token' : owner_id,
        'u_ids' : [user_id, user_id_1]
        })
    response_1 = requests.get(config.url + 'dm/list/v1', params={'token': owner_id})
    dm = response_1.json()['dms']
    assert dm ==  [{'dm_id': 0, 'name': 'johnsmith, johnsmith0, johnsmith1'}]
    assert response.status_code == SUCCESS

# test one user with multiple dms
def test_dm_list_multiple_dms_one_user():
    '''
    Test the list of 1 user in many dms
    '''
    user_1 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email1@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
    })
    user_1_token = user_1.json()['token']
    user_2 = requests.post(config.url + 'auth/register/v2', json={
            'email': 'email2@gmail.com',
            'password': 'password',
            'name_first': 'John',
            'name_last': 'Smith',
        })
    user_2_token = user_2.json()['token']

    create_dm_1 = requests.post(config.url + 'dm/create/v1', json={
            'token': user_1_token,
            'u_ids': [],
        })
    assert create_dm_1.status_code == SUCCESS

    create_dm_2 = requests.post(config.url + 'dm/create/v1', json={
            'token': user_1_token,
            'u_ids': [],
        })

    assert create_dm_2.status_code == SUCCESS

    response_1 = requests.get(config.url + 'dm/list/v1', params={'token': user_1_token})
    assert response_1.status_code == SUCCESS

    expected_dm_list_1 = [{'dm_id': 0, 'name': 'johnsmith'}, {'dm_id': 1, 'name': 'johnsmith'}]
    actual_dm_list_1 = response_1.json()['dms']

    # user 1 should be in 2 dms
    assert expected_dm_list_1 == [{'dm_id': 0, 'name': 'johnsmith'}, {'dm_id': 1, 'name': 'johnsmith'}]
    assert sorted(actual_dm_list_1, key=lambda d: d['dm_id']) == sorted(expected_dm_list_1, key=lambda d: d['dm_id'])
    # user 2 not be in any dms
    response_2 = requests.get(config.url + 'dm/list/v1', params={'token': user_2_token})
    assert response_2.status_code == SUCCESS
    dm_list_2 = response_2.json()['dms']

    assert dm_list_2 == []
