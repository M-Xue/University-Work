from src.data_store import data_store 
from src.error import InputError, AccessError
from src.security_helper import decode_jwt, check_valid_token
from src.helper import find_user, find_user_in_channel, is_global_owner, is_user_channel_owner, count_seams_owners, store_history
from random import choice
import string
VALID_PERMISSION_IDS = [1, 2]
SUCCESS = 200
INPUT_ERROR = 400
ACCESS_ERROR = 403
OWNER_PERMISSION_ID = 1
MEMBER_PERMISSION_ID = 2

def admin_userpermission_change_v1(token, u_id, permission_id):
    '''
    Allows an owner to change the permission id of another user

    Arguments:
    token           (string) - token of an authorised user of Seams
    u_id            (int)    - id of a user
    permission_id   (int)    - desired permission id for given u_id

    Exceptions:
    InputError  - Occurs when: > u_id refers to the only global owner being demoted to a member
                               > u_id does not refer to a valid user
                               > permission_id is invalid
                               > u_id already has specified permission id

    AccessError - Occurs when: > token is not of an authorised global owner
                               > token is invalid

    Return Value:
    Returns {} on token successfully makinng another user a member of the channel

    '''

    # Check if token is valid, and if they have permission then DO NEXT
    check_valid_token(token)
    user_id = decode_jwt(token)['auth_user_id']
    #store_history(user_id, admin_userpermission_change_v1)
    user = find_user(user_id)

    if user['permission_id'] == MEMBER_PERMISSION_ID:
        raise AccessError(description='User is not authorised to change permissions')

    store = data_store.get()
    users = store['users']

    if permission_id not in VALID_PERMISSION_IDS:
        raise InputError(description="Permissions ID is invalid")
    
    # Count amount of current seams owners
    owner_counter = count_seams_owners()


    for person in users:
        # Loop until matching User ID
        if u_id == person['u_id']:
            # Check if user already has specified permission ID
            if person['permission_id'] == permission_id:
                raise InputError(description='User already has specified permission ID')

            # Must be at least 1 global owner after changing permissions
            elif person['permission_id'] == OWNER_PERMISSION_ID and owner_counter == 1:
                raise InputError(description="User is the only global owner so they can't be demoted to a member")

            else:
                person['permission_id'] = permission_id
                data_store.set(store)
                return {}

    # Couldn't find user
    raise InputError(description="User ID doesn't exist")

def admin_user_remove_v1(token, u_id):
    '''
    Allows a seams owner to remove another user from Seams entirely.
    That user will be removed from all channels/dms and will not be
    in users/all.
    Contents of the messages they sent will be replaced
    with 'Removed user'.
    User/profile is retrievable but name will be 'Replaced User'

    Arguments:
    token           (string) - token of an authorised user of Seams
    u_id            (int)    - id of user to be removed

    Exceptions:
    InputError  - Occurs when: > u_id refers to the only global owner
                               > u_id does not refer to a valid user

    AccessError - Occurs when: > token is not of an authorised global owner
                               > token is invalid

    Return Value:
    Returns {} on token successfully makinng another user a member of the channel

    '''
    check_valid_token(token)
    auth_user_id = decode_jwt(token)['auth_user_id']

    if is_global_owner(auth_user_id) == False:
        raise AccessError(description="User is not authorised to remove others")
    
    if find_user(u_id) == None:
        raise InputError(description="Invalid user ID")

    # Returns dictionary of user wanting to be removed
    removed_user = find_user(u_id)

    owner_counter = count_seams_owners()
    # If they are the only global owner left, and is trying to be removed
    if owner_counter == 1 and removed_user['permission_id'] == 1:
        raise InputError(description="The last seams owner cannot be removed")

    store = data_store.get()
    messages = store['messages']
    dm_messages = store['dm_messages']
    channels = store['channels']
    dms = store['dms']

    # Change all their sent messages to 'Removed user'
    for message in messages:
        if message['auth_user_id'] == u_id:
            message['message'] = 'Removed user'
    
    for dm_message in dm_messages:
        if dm_message['auth_user_id'] == u_id:
            dm_message['message'] = 'Removed user'


    # Remove user from Channel
    for channel in channels:
        # Check if user is in any channels
        if find_user_in_channel(u_id, channel) != None:

            channel['all_members'].remove({
            'u_id': u_id,
            'email': removed_user['email'],
            'name_first': removed_user['name_first'],
            'name_last': removed_user['name_last'],
            'handle_str': removed_user['handle_str']
            })
            
            # If they are the owner of any channels
            if is_user_channel_owner(removed_user, channel) == True:
                channel['owner_members'].remove({
                'u_id': u_id,
                'email': removed_user['email'],
                'name_first': removed_user['name_first'],
                'name_last': removed_user['name_last'],
                'handle_str': removed_user['handle_str']
                })
                

    # Remove user from DM
    for dm in dms:
        if u_id in dm['u_ids']:
            dm['u_ids'].remove(u_id)

    # Change users name to 'Removed'
    removed_user['name_first'] = 'Removed'
    removed_user['name_last'] = 'user'

    # Make emails and handle reusable by making their email and handle random
    removed_user['email'] = ''.join([choice(string.ascii_letters) for _ in range (20)])
    removed_user['handle_str'] = ''.join([choice(string.ascii_letters) for _ in range (20)])

    # Then invalidate the token by removing their session
    sessions = store['sessions']
    for session in sessions:
        if session['user_id'] == u_id:
            sessions.remove(session)
    store_history(auth_user_id, admin_user_remove_v1)
    return {}



