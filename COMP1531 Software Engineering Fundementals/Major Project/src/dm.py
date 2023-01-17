from src.data_store import data_store
from src.error import AccessError, InputError
from src.notifications import channel_add_notif_dm
from src.security_helper import decode_jwt, check_valid_token
from src.helper import find_user, find_user_in_dm_id, is_u_ids_valid, find_user_in_dms, find_dm, user_is_not_original_creator, check_if_owner_id, store_history
import copy
'''
Functions for dms
'''

def dm_list_v1(token):
    '''
    Returns the list of DMs that the user is a member of.
    Arguments:
    token    (string)    - id of an authorised user of Seams

    Exceptions:
    AccessError - Occurs when: > token given is invalid

    Return Value:
    Returns {'dms}
    '''
    # exception check
    # if token is not valid, raise access error
    check_valid_token(token)

    data = data_store.get()
    dms_list = data['dms']
    result_dms = []
    auth_user_id = decode_jwt(token)['auth_user_id']

    for dm in dms_list:
        # check if user is part of that dm
        if find_user_in_dms(auth_user_id, dm) is True:
        # append to list
            result_dms.append({
                'dm_id' : dm['dm_id'],
                'name' : dm['name']
            })

    return {"dms" : result_dms}

def dm_create_v1(token, u_ids):
    '''
    u_ids contains the user(s) that this DM is directed to, and will not include
    the creator. The creator is the owner of the DM. name should be
    automatically generated based on the users that are in this DM. The name
    should be an alphabetically-sorted, comma-and-space-separated list of user
    handles, e.g.'ahandle1, bhandle2, chandle3'.

    Arguments:
    token    (string)    - id of an authorised user of Seams
    u_ids      (list)    - list of users ids

    Exceptions:
    InputError  - Occurs when: > any u_id in u_ids does not refer to a valid user
                               > there are duplicate 'u_id's in u_ids

    AccessError - Occurs when: > token given is invalid

    Return Value:
    Returns {'dm_id}
    '''

    data = data_store.get()

    dms = data['dms']
    # exception check
    # if token is not valid, raise access error
    check_valid_token(token)
    # if u_ids are invalid, raise input error
    if not is_u_ids_valid(u_ids):
        raise InputError(description='u_id is invalid')
    # if u_ids are duplicate, raise input error
    if any(u_ids.count(user) > 1 for user in u_ids):
        raise InputError(description='There are duplicate u_ids')

    auth_user_id = decode_jwt(token)['auth_user_id']

    dm_names = []

    if len(u_ids) == 0:
        dm_names = [find_user(auth_user_id)['handle_str']]
    else:
        for u_id in u_ids:
            user_handle = find_user(int(u_id))['handle_str']
            dm_names.append(user_handle)
        dm_names += [find_user(auth_user_id)['handle_str']]

    dm_name_result = ', '.join(sorted(dm_names))
    # u_ids include the owner now
    new_u_ids = u_ids + [auth_user_id]
    new_dm = {
            'dm_id' : len(dms),
            'name' : dm_name_result,
            'owners_id' : auth_user_id,
            'u_ids' : new_u_ids,
            'standup_active' : False,
            'standup_time_finish' : None,
            'standup_start_user' : None,
            'standup_messages' : [],
    }
    dms.append(new_dm)

    data['dms'] = dms
    data_store.set(data)

    #raise notifs when added to dm
    for user in u_ids:
        channel_add_notif_dm(user, auth_user_id, (len(dms)-1))

    store_history(auth_user_id, dm_create_v1, u_ids)
    
    return {'dm_id': (len(dms)-1)}

def dm_remove_v1(token, dm_id):
    '''
    Remove an existing DM, so all members are no longer in the DM.
    This can only be done by the original creator of the DM.

    Arguments:
    token    (string)    - id of an authorised user of Seams
    dm_id      (int)     - dm_id of the dm

    Exceptions:
    InputError  - Occurs when: > dm_id does not refer to a valid DM

    AccessError - Occurs when: > dm_id is valid and the authorised user is not the original DM creator
                               > dm_id is valid and the authorised user is no longer in the DM
                               > token given is invalid
    Return Value:
    Returns {}
    '''

    # Input Error when dm_id is invalid
    if find_dm(dm_id) is None:
        raise InputError(description='dm_id does not refer to a valid DM')

    # invalid token
    check_valid_token(token)

    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # Access Error when dm_id is valid and user is not in dm
    if ((find_dm(dm_id) is not None) and (find_user_in_dms(auth_user_id, find_dm(dm_id)) is False)):
        raise AccessError(description='dm_id is valid and authorised user is not a member of the DM')


    # Access Error when dm_id is valid and user is not original dm creator
    if user_is_not_original_creator(auth_user_id, dm_id) is False:
        raise AccessError(description='dm_id is valid and authorised user is not dm creator')

    data = data_store.get()
    dms = data['dms']
    store_history(auth_user_id, dm_remove_v1, [dm_id])
    # remove existing dm
    dm = find_dm(dm_id)
    dms.remove(dm)

    return {}

def dm_details_v1(token, dm_id):
    '''
    Given a DM with ID dm_id that the authorised user is a member of, provide basic
    details about the DM.

    Arguments:
    token    (string)    - id of an authorised user of Seams
    dm_id      (int)     - dm_id of the dm

    Exceptions:
    InputError  - Occurs when: > dm_id does not refer to a valid DM

    AccessError - Occurs when: > dm_id is valid and the authorised user is not a member of the DM
                               > token given is invalid
    Return Value:
    Returns {name, members}
    '''

# InputError dm_id does not refer to a valid DM
    if find_dm(dm_id) is None:
        raise InputError(description='dm_id does not refer to a valid DM')

    # Access Error token given is invalid
    check_valid_token(token)

    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

# AccessError is dm_id is valid and the authorised user is not a member of the DM
    if ((find_dm(dm_id) is not None) and (find_user_in_dms(auth_user_id, find_dm(dm_id)) is False)):
        raise AccessError(description='dm_id is valid and authorised user is not a member of the DM')

    dm = find_dm(dm_id)

    members_list = []
    for member_id in dm['u_ids']:
        user = find_user(member_id)
        members_list.append({
            'u_id': user['u_id'],
            'email': user['email'],
            'name_first': user['name_first'],
            'name_last': user['name_last'],
            'handle_str': user['handle_str']
        })

    return {'name': dm['name'], 'members': members_list }

def dm_leave_v1(token, dm_id):
    '''
    Allows an authorised user to leave a dm

    Arguments:
    token       (string)    - token of an authorised user of Seams
    dm_id  (int)            - id of a dm in Seams

    Exceptions:
    InputError  - Occurs when dm_id does not refer to a valid dm

    AccessError - Occurs when: > dm_id is valid and token does not refer to an auth_user_id
                                 that is already a member of the dm
                                token is not a valid token

    Return Value:
    Returns {} on authorised user successfully leaving the dm

    '''
    # If dm is not a valid dm
    if find_dm(dm_id) is None:
        raise InputError(description='Invalid dm_id')

    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If dm_id is valid and auth_user is not a member of the dm
    if ((find_dm(dm_id) is not None) and
        (find_user_in_dm_id(auth_user_id, dm_id) is None)):
        raise AccessError(description='User is not a member of dm')

    # Remove user from u_ids
    dm = find_dm(dm_id)
    dm['u_ids'].remove(auth_user_id)
    store_history(auth_user_id, dm_leave_v1)
    return {}
    
def dm_messages_v1(token, dm_id, start):
    """
        Given a DM with ID dm_id that the authorised user is a member of,
        return up to 50 messages between index "start" and "start + 50".

    Arguments:
        token     - <auth_user_id of user>
        dm_id     - <channel_id of channel>
        start     - <start of msg>

    Exceptions:
        InputError  - Occurs when dm_id does not refer to a valid dm
        InputError  - Occrus when start is greater than the total number of messages in the dm
        AccessError - Occurs when dm_id is valid and the authorised user is not a member of the dm

    Return Value:
        Returns {messages, start, end} on passing all exceptions
    """
    #get data_store to store in data
    store = data_store.get()
    dm_messages = store['dm_messages']

    # Check if token is invalid
    check_valid_token(token)
    # get auth_user_id from token
    auth_user_id = decode_jwt(token)['auth_user_id']

    # If dm_id not valid
    if find_dm(dm_id) is None:
        raise InputError(description='Not valid dm_id')

    # If dm_id is valid and auth_user is not a member of the dm
    if not check_if_owner_id(auth_user_id, dm_id) and not find_user_in_dms(auth_user_id, find_dm(dm_id)):
        raise AccessError(description="dm_id is valid and the authorised user is not a member of the DM")

    #input error when start is greater than the total number of messages in the channel
    #assumed message start from 0 (no greeting msg)
    tot_msg = 0
    for message in dm_messages:
        #count number of msgs
        if message['dm_id'] == dm_id:
            tot_msg += 1
    #compare amount
    if tot_msg < start:
        raise InputError(description="start is greater than the total number of messages in the channel")

    #manage end value
    end = 0
    if tot_msg - start < 50:
        end = -1
    else:
        end = start + 50
    
    #reverse messages to start from the most recent
    #make a copy to not reverse it for other functions
    copy_store = copy.deepcopy(store)
    copy_store['dm_messages'].reverse()

    index = 0
    dm_list = []
    for dm_message in copy_store['dm_messages']:
        if dm_message['dm_id'] == dm_id:
            # if start <= index <= start +50
            if start <= index and index <= start +49:
                #create dict
                message = {}
                #create copy of message['reacts']
                copy_reacts = dm_message.copy()
                for react in copy_reacts['reacts']:
                    if auth_user_id in react['u_ids']:
                        react.update({'is_this_user_reacted': True})
                    else:
                        react.update({'is_this_user_reacted': False})
                message['message_id'] = dm_message['message_id']
                message['u_id'] = dm_message['auth_user_id']
                message['message'] = dm_message['message']
                message['time_sent'] = dm_message['time_sent']
                message['reacts'] = copy_reacts['reacts']
                message['is_pinned'] = dm_message['is_pinned']
                dm_list.append(message)
            index += 1
    return {
        'messages': dm_list,
        'start': start,
        'end': end
    }


