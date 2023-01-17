import json
import time
import requests
import pytest
from src import config
INPUT_ERROR = 400
ACCESS_ERROR = 403
SUCCESS = 200

@pytest.fixture(autouse=True)
def clear():
    requests.delete(config.url + "clear/v1")

########## SEARCH_V1 TESTS ##########

def test_search_InputError_invalid_query_str():
    # Create user
    user = requests.post(config.url + "auth/register/v2", json={
        'email': 'email@gmail.com',
        'password': 'password',
        'name_first': 'first',
        'name_last': 'last',
    })
    token = user.json()['token']

    # User tries to search for a message with over 1000 characters
    search_request = requests.get(config.url + "search/v1", params={
        'token': token,
        'query_str': 'hi'*1000,
    })
    assert search_request.status_code == INPUT_ERROR

    # User tries to search for a message with less than 1 character
    search_request = requests.get(config.url + "search/v1", params={
        'token': token,
        'query_str': '',
    })
    assert search_request.status_code == INPUT_ERROR

def test_search_AccessError_invalid_token():
    # Invalid token tries to search for a message
    search_request = requests.get(config.url + "search/v1", params={
        'token': "invalid",
        'query_str': 'hi',
    })
    assert search_request.status_code == ACCESS_ERROR

def test_search_normal_functioning():
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

    # User sends message
    requests.post(config.url + "message/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': "hi",
    })

    # Get details of message for checking
    check = requests.get(config.url + "channel/messages/v2", params={
        'token': token,
        'channel_id': channel_id,
        'start': 0,
    })
    msg = check.json()['messages'][0]

    # User searches for message containing 'hi'
    search_request = requests.get(config.url + "search/v1", params={
        'token': token,
        'query_str': "hi",
    })
    assert search_request.status_code == SUCCESS
    assert search_request.json() == {'messages': [msg]}

def test_search_no_message_found_functioning():
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

    # User sends message
    requests.post(config.url + "message/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': "hi",
    })

    # User searches for message containing 'hello'
    search_request = requests.get(config.url + "search/v1", params={
        'token': token,
        'query_str': "hello",
    })
    assert search_request.status_code == SUCCESS
    assert search_request.json() == {'messages': []}

def test_search_multiple_messages_functioning():
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

    ## TEST FOR CHANNEL ##
    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User2 joins channel
    requests.post(config.url + 'channel/join/v2', json={
        'token': token2,
        'channel_id': channel_id
    })

    # User1 sends a channel message
    message1 = requests.post(config.url + "message/send/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'message': "hi",
    })
    msg1_id = message1.json()['message_id']

    # Get details of message for checking
    check = requests.get(config.url + "channel/messages/v2", params={
        'token': token1,
        'channel_id': channel_id,
        'start': 0,
    })
    msg1 = check.json()['messages'][0]

    ## TEST FOR DM ##
    # Create DM by user2 with members: user1, user2
    dm = requests.post(config.url + "dm/create/v1", json={
        'token': token2, 
        'u_ids':[u_id1],
    })
    dm_id = dm.json()['dm_id']

    # User2 sends a DM message
    message2 = requests.post(config.url + "message/senddm/v1", json={
        'token': token2,
        'dm_id': dm_id,
        'message': "This message says hi",
    })
    msg2_id = message2.json()['message_id']

    # Get details of message for checking
    check = requests.get(config.url + "dm/messages/v1", params={
        'token': token2,
        'dm_id': dm_id,
        'start': 0,
    })
    msg2 = check.json()['messages'][0]

    ## TEST FOR CHANNEL MESSAGE SHARE ##
    # User2 shares user1's message in channel
    requests.post(config.url + "/message/share/v1", json={
        'token': token2,
        'og_message_id': msg1_id,
        'message': 'sharing msg1',
        'channel_id': channel_id, 
        'dm_id': -1,
    })

    # Get details of message for checking
    check = requests.get(config.url + "channel/messages/v2", params={
        'token': token2,
        'channel_id': channel_id,
        'start': 0,
    })
    msg3 = check.json()['messages'][0]

    ## TEST FOR DM MESSAGE SHARE ##
    # User1 shares user2's message in DM
    requests.post(config.url + "/message/share/v1", json={
        'token': token1,
        'og_message_id': msg2_id,
        'message': 'sharing msg2',
        'channel_id': -1, 
        'dm_id': dm_id,
    })

    # Get details of message for checking
    check = requests.get(config.url + "dm/messages/v1", params={
        'token': token2,
        'dm_id': dm_id,
        'start': 0,
    })
    msg4 = check.json()['messages'][0]

    # User1 searches for messages containing 'hi'
    search_request = requests.get(config.url + "search/v1", params={
        'token': token1,
        'query_str': "hi",
    })
    assert search_request.status_code == SUCCESS
    assert search_request.json() == {'messages': [msg1, msg3, msg2, msg4]}

def test_search_not_member_of_some_channels_functioning():
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

    # Create channel with user1
    channel = requests.post(config.url + "channels/create/v2", json={
        'token': token1, 
        'name': 'user1_channel', 
        'is_public': True,
    })
    channel_id = channel.json()['channel_id']

    # User1 sends a channel message
    requests.post(config.url + "message/send/v1", json={
        'token': token1,
        'channel_id': channel_id,
        'message': "channel message: hi",
    })

    # Create DM by user2 with members: user1, user2
    dm = requests.post(config.url + "dm/create/v1", json={
        'token': token2, 
        'u_ids':[u_id1],
    })
    dm_id = dm.json()['dm_id']

    # User2 sends a DM message
    requests.post(config.url + "message/senddm/v1", json={
        'token': token2,
        'dm_id': dm_id,
        'message': "DM message: hi",
    })

    # Get details of message for checking
    check = requests.get(config.url + "dm/messages/v1", params={
        'token': token2,
        'dm_id': dm_id,
        'start': 0,
    })
    dm_msg = check.json()['messages'][0]

    # User2 searches for messages containing 'hi'
    search_request = requests.get(config.url + "search/v1", params={
        'token': token2,
        'query_str': "hi",
    })
    assert search_request.status_code == SUCCESS
    print(search_request.json())
    assert search_request.json() == {'messages': [dm_msg]}

def test_search_submatching_functioning():
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

    # User sends message
    requests.post(config.url + "message/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': "HI",
    })

    # Get details of message for checking
    check = requests.get(config.url + "channel/messages/v2", params={
        'token': token,
        'channel_id': channel_id,
        'start': 0,
    })
    msg1 = check.json()['messages'][0]

    # User sends another message
    requests.post(config.url + "message/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': "tHIs message also should be included in search",
    })

    # Get details of message for checking
    check = requests.get(config.url + "channel/messages/v2", params={
        'token': token,
        'channel_id': channel_id,
        'start': 0,
    })
    msg2 = check.json()['messages'][0]

    # User searches for message containing 'hi'
    search_request = requests.get(config.url + "search/v1", params={
        'token': token,
        'query_str': "HI",
    })
    assert search_request.status_code == SUCCESS
    assert search_request.json() == {'messages': [msg1, msg2]}

def test_search_reacted_and_pinned_messages_functioning():
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

    # User sends message
    message = requests.post(config.url + "message/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': "HI",
    })
    msg1_id = message.json()['message_id']

    # User reacts to message
    requests.post(config.url + "message/react/v1", json={
        'token': token,
        'message_id': msg1_id,
        'react_id': 1,
    })

    # Get details of message for checking
    check = requests.get(config.url + "channel/messages/v2", params={
        'token': token,
        'channel_id': channel_id,
        'start': 0,
    })
    msg1 = check.json()['messages'][0]

    # User sends another message
    message = requests.post(config.url + "message/send/v1", json={
        'token': token,
        'channel_id': channel_id,
        'message': "tHIs message also should be included in search",
    })
    msg2_id = message.json()['message_id']

    # User pins message
    requests.post(config.url + "message/pin/v1", json={
        'token': token,
        'message_id': msg2_id,
    })

    # Get details of message for checking
    check = requests.get(config.url + "channel/messages/v2", params={
        'token': token,
        'channel_id': channel_id,
        'start': 0,
    })
    msg2 = check.json()['messages'][0]

    # User searches for message containing 'hi'
    search_request = requests.get(config.url + "search/v1", params={
        'token': token,
        'query_str': "HI",
    })
    assert search_request.status_code == SUCCESS
    assert search_request.json() == {'messages': [msg1, msg2]}
