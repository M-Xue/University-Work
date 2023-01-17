import json
import requests
import pytest
from src import config

INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

########## CHANNEL_ADDOWNER_V1 TESTS ##########

# Test for InputError when channel_id does not refer to a valid channel
def test_channel_addowner_v1_InputError_invalid_channel():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    u_id2 = user2.json()['auth_user_id']

    # User1 tries to add user2 as owner to invalid channel
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token1,
        'channel_id': 123,
        'u_id': u_id2,
    })
    assert add_request.status_code == INPUT_ERROR

# Test for InputError when u_id does not refer to a valid user
def test_channel_addowner_v1_InputError_invalid_user():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # Create channel with user
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token, 
        'name': 'user_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User tries to add an invalid u_id as an owner to channel
    invite_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token,
        'channel_id': channel_id,
        'u_id': 123,
    })
    assert invite_request.status_code == INPUT_ERROR

# Test for InputError when u_id is not a member of the channel
def test_channel_addowner_v1_InputError_existing_member():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    u_id2 = user2.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User1 tries to add user2 as owner but user2 is not already a member in the channel
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'u_id': u_id2,
    })
    assert add_request.status_code == INPUT_ERROR

# Test for InputError when u_id is already a owner of the channel
def test_channel_addowner_v1_InputError_existing_owner():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']
    u_id2 = user2.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})
    
    # User1 adds user2 as a channel owner
    requests.post(config.url + "channel/addowner/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'u_id': u_id2,
    })

    # User1 tries to add user2 as a channel owner when user2 already is
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'u_id': u_id2,
    })
    assert add_request.status_code == INPUT_ERROR

# Test for AccessError when channel_id is valid and token does not have owner permissions in the channel
def test_channel_addowner_v1_AccessError():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']

    # Create user3
    user3 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email3@gmail.com',
        'password': 'password3',
        'name_first': 'first3',
        'name_last': 'last3',
    })
    token3 = user3.json()['token']
    u_id3 = user3.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})

    # User3 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token3, 'channel_id': channel_id})

    # User2 tries to add user3 as an owner of the channel
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token2,
        'channel_id': channel_id,
        'u_id': u_id3,
    })
    assert add_request.status_code == ACCESS_ERROR

# Test for AccessError when an invalid token is given
def test_channel_addowner_v1_AccessError_invalid_token():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']
    u_id2 = user2.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})

    # Invalid token tries to add user2 as owner of channel
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': "invalid",
        'channel_id': channel_id,
        'u_id': u_id2,
    })
    assert add_request.status_code == ACCESS_ERROR

# Test for AccessError when token is not a member of the channel
def test_channel_addowner_v1_AccessError_token_invalid_member():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']

    # Create user3
    user3 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email3@gmail.com',
        'password': 'password3',
        'name_first': 'first3',
        'name_last': 'last3',
    })
    token3 = user3.json()['token']
    u_id3 = user3.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User3 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token3, 'channel_id': channel_id})

    # User2 tries to add user3 as an owner of the channel
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token2,
        'channel_id': channel_id,
        'u_id': u_id3,
    })
    assert add_request.status_code == ACCESS_ERROR

# Test for normal functioning
def test_channel_addowner_v1_functioning():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']
    u_id2 = user2.json()['auth_user_id']

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token2, 'channel_id': channel_id})

    # User1 adds user2 as owner of channel
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'u_id': u_id2,
    })
    assert add_request.status_code == SUCCESS
    assert add_request.json() == {}

# Test for global owner having owner permissions without being an owner
def test_global_owner_member_can_addowner():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']
    u_id1 = user1.json()['auth_user_id']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']

    # Create channel with user2
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token2, 
        'name': 'user2_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User1 (global owner) joins channel
    requests.post(config.url + "channel/join/v2", json={'token': token1, 'channel_id': channel_id})

    # User1 adds themselves as owner
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'u_id': u_id1,
    })
    assert add_request.status_code == SUCCESS
    assert add_request.json() == {}

def test_non_member_cannot_add_owner():
    # Create user1
    user1 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email1@gmail.com',
        'password': 'password1',
        'name_first': 'first1',
        'name_last': 'last1',
    })
    token1 = user1.json()['token']
    u_id1 = user1.json()['auth_user_id']

    # Create user2
    user2 = requests.post(config.url + "auth/register/v2", json={
        'email': 'email2@gmail.com',
        'password': 'password2',
        'name_first': 'first2',
        'name_last': 'last2',
    })
    token2 = user2.json()['token']

    # Create channel with user2
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token2, 
        'name': 'user2_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User1 adds themselves as owner
    add_request = requests.post(config.url + "channel/addowner/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'u_id': u_id1,
    })
    assert add_request.status_code == ACCESS_ERROR
