## Auth Assumptions  
- any characters can be used for names because specs talk about removing non-alphanumeric characters from names  
- names and passwords can be the same between differen users  
- users cannot be deleted  

## CHANNELS  
- The user that creates a channel automatically becomes the owner of that channel  
- Global owners can join private channels without an invite  

## CHANEL_ID ASSUMPTIONS
- The channel_id starts at 1 instead of 0, and increments by 1  

## messages  
-no greeting msg appear, therefore the initial number of messages is 0  

## Channel_Details  
- Putting in no input into channel lists will not work  
- Putting in non-integer inputs into channel_details will not work  

## Channel_removeowner
- Owners can remove themselves as owners

## Users/all/v1
- If you register your user with names 'Removed user', you will not be called via users/all/v1

## Admin/user/remove
- Removing a user will generate random 20 character strings for their email and handle, breaking the 'conditions' for valid email and handles.

## auth/logout/v1
- Assuming you cannot log out with the same token twice

## DM Assumtions
- dm_id starts from 0
- For dms in data_store, u_ids includes the owner

## user/stats
- I do not tests for whether the timestamps match, as it will be unreliable based on the time it takes for the processor to run the function and create the timestamp.
  This will make it hard to assert whether the timestamps are equal.
- A user will still be "joined in a DM" if it was part of a DM that got removed

## Search
- the messages that are found are unsorted
- the user is only given messages for channels/DMs that they are curently a member of (not previous)

## Password Reset Codes
- no more than 900000 users will be requesting a password reset code at any one time
