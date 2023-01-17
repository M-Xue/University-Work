import pytest

from src.channel import channel_invite_v1, channel_details_v1, channel_join_v1
from src.channels import channels_create_v1
from src.error import InputError, AccessError
from src.other import clear_v1
from src.auth import auth_register_v1
from src.helper import find_user

########### CHANNEL_INVITE_V1 TESTS ###########

# Test for InputError when channel_id does not refer to a valid channel
def test_channel_invite_v1_InputError_invalid_channel():
    clear_v1()
    user_1 = auth_register_v1('email1@gmail.com', 'password1', 'first1', 'last1')
    user_2 = auth_register_v1('email2@gmail.com', 'password2', 'first2', 'last2')
    channel = {
                "channel_id" : 123,
                "name" : "Channel_1"
            }
    with pytest.raises(InputError):
        channel_invite_v1(user_1, channel, user_2)

# Test for InputError when u_id does not refer to a valid user
def test_channel_invite_v1_InputError_invalid_user():
    clear_v1()
    user_1 = auth_register_v1('email1@gmail.com', 'password1', 'first1', 'last1')
    channel_1 = channels_create_v1(user_1['auth_user_id'], 'user_1_channel', True)
    user_2 = {
        'auth_user_id': 123,
        'email': "email@gmail.com",
        'name_first': 'first',
        'name_last': 'last',
        'handle_str': 'firstlast',
        'password': 'password',
        'global_owner': False,
    }
    with pytest.raises(InputError):
        channel_invite_v1(user_1['auth_user_id'], channel_1['channel_id'], user_2['auth_user_id'])

# Test for InputError when u_id is already a member of the channel
def test_channel_invite_v1_InputError_existing_member():
    clear_v1()
    user_1 = auth_register_v1('email1@gmail.com', 'password1', 'first1', 'last1')
    user_2 = auth_register_v1('email2@gmail.com', 'password2', 'first2', 'last2')
    channel_1 = channels_create_v1(user_1['auth_user_id'], 'user_1_channel', True)
    channel_join_v1(user_2['auth_user_id'], channel_1['channel_id'])
    with pytest.raises(InputError):
        channel_invite_v1(user_1['auth_user_id'], channel_1['channel_id'], user_2['auth_user_id'])

# Test for AccessError
def test_channel_invite_v1_AccessError():
    clear_v1()
    user_1 = auth_register_v1('email1@gmail.com', 'password1', 'first1', 'last1')
    user_2 = auth_register_v1('email2@gmail.com', 'password2', 'first2', 'last2')
    user_3 = auth_register_v1('email3@gmail.com', 'password3', 'first3', 'last3')
    channel_1 = channels_create_v1(user_1['auth_user_id'], 'user_1_channel', True)
    with pytest.raises(AccessError):
        channel_invite_v1(user_2['auth_user_id'], channel_1['channel_id'], user_3['auth_user_id'])

# Test for normal functioning
def test_channel_invite_v1_functioning():
    clear_v1()
    user_1 = auth_register_v1('email1@gmail.com', 'password1', 'first1', 'last1')
    user_2 = auth_register_v1('email2@gmail.com', 'password2', 'first2', 'last2')
    user_3 = auth_register_v1('email3@gmail.com', 'password3', 'first3', 'last3')
    u_1 = find_user(user_1['auth_user_id'])
    u_2 = find_user(user_2['auth_user_id'])
    u_3 = find_user(user_3['auth_user_id'])
    channel_1 = channels_create_v1(user_1['auth_user_id'], 'user_1_channel', True) # public channel
    channel_invite_v1(user_1['auth_user_id'], channel_1['channel_id'], user_2['auth_user_id']) # user_1 invites user_2 to channel
    assert channel_details_v1(user_2['auth_user_id'], channel_1['channel_id']) == {
        'name': 'user_1_channel',
        'is_public': True,
        'owner_members': [
            {
                'u_id': user_1['auth_user_id'],
                'email': u_1['email'],
                'name_first': u_1['name_first'],
                'name_last': u_1['name_last'],
                'handle_str': u_1['handle_str'],
            }
        ],
        'all_members': [
            {
                'u_id': user_1['auth_user_id'],
                'email': u_1['email'],
                'name_first': u_1['name_first'],
                'name_last':  u_1['name_last'],
                'handle_str': u_1['handle_str'],
            },
            {
                'u_id': user_2['auth_user_id'],
                'email': u_2['email'],
                'name_first': u_2['name_first'],
                'name_last':  u_2['name_last'],
                'handle_str': u_2['handle_str'],
            }
        ],
    }
    channel_invite_v1(user_2['auth_user_id'], channel_1['channel_id'], user_3['auth_user_id']) # user_2 invites user_3 to channel

    assert channel_details_v1(user_3['auth_user_id'], channel_1['channel_id']) == {
        'name': 'user_1_channel',
        'is_public': True,
        'owner_members': [
            {
                'u_id': user_1['auth_user_id'],
                'email': u_1['email'],
                'name_first': u_1['name_first'],
                'name_last':  u_1['name_last'],
                'handle_str': u_1['handle_str'],
            }
        ],
        'all_members': [
            {
                'u_id': user_1['auth_user_id'],
                'email': u_1['email'],
                'name_first': u_1['name_first'],
                'name_last':  u_1['name_last'],
                'handle_str': u_1['handle_str'],
            },
            {
                'u_id': user_2['auth_user_id'],
                'email': u_2['email'],
                'name_first': u_2['name_first'],
                'name_last':  u_2['name_last'],
                'handle_str': u_2['handle_str'],
            },
            {
                'u_id': user_3['auth_user_id'],
                'email': u_3['email'],
                'name_first': u_3['name_first'],
                'name_last':  u_3['name_last'],
                'handle_str': u_3['handle_str'],
            },
        ],
    }
    channel_2 = channels_create_v1(user_2['auth_user_id'], 'user_2_priv_channel', False) # private channel
    channel_invite_v1(user_2['auth_user_id'], channel_2['channel_id'], user_3['auth_user_id'])
    assert channel_details_v1(user_3['auth_user_id'], channel_2['channel_id']) == {
        'name': 'user_2_priv_channel',
        'is_public': False,
        'owner_members': [
            {
                'u_id': user_2['auth_user_id'],
                'email': u_2['email'],
                'name_first': u_2['name_first'],
                'name_last':  u_2['name_last'],
                'handle_str': u_2['handle_str'],
            }
        ],
        'all_members': [
            {
                'u_id': user_2['auth_user_id'],
                'email': u_2['email'],
                'name_first': u_2['name_first'],
                'name_last':  u_2['name_last'],
                'handle_str': u_2['handle_str'],
            },
            {
                'u_id': user_3['auth_user_id'],
                'email': u_3['email'],
                'name_first': u_3['name_first'],
                'name_last':  u_3['name_last'],
                'handle_str': u_3['handle_str'],
            }
        ],
    }
