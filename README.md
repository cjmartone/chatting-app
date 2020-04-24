# chatting-app
CS 656 Programming Project

# Layouts
## conversation.xml
This is the screen where the messages between you and the selected friends are displayed.
Holds the message_view RecyclerView that auto-retreives and displays new messages in real time.

## find_friend_screen.xml
This is the screen where users can search for and add new friends

## home_screen.xml
This is the screen that lists all the friends. Tapping on one will open up to conversation.xml.
Users can also get to find_friend_screen.xml by tapping "Add friends".

## message.xml
This is the layout that the RecyclerView uses when displaying a new message.
It contains the name of the sender, message body (if applicable), a sent image (if applicable), a recorded audio (if applicable),
and a timestamp.

## recording_screen.xml
This is the screen where the user can record audio and send it to their friend.

# MainActivity
Loads the home_screen.xml layout to display the user's current friends. If no user is signed in, they will be prompted for an email address. If the email is registered under an existing user, then they will be prompted for a password. Otherwise, the user will be prompted to create an account. 
## Adding friends
Tapping on the "Add friends" button will load the FindFriendsActivity which allows the user to search for and add friends.
## Sending messages
Tapping on a friend under the main layout (Should say "Contacts" on the top ofthe screen) will create the MessageScreenActivity.

# FindFriendsActivity
Loads the find_friend_screen.xml layout to provide users a way to search for friends to add. Try searching for "Test User" and add one of them. They will appear when you go back to the main layout.

# MessageScreenActivity
Loads the messages_screen.xml layout to display the conversation between the user and the selected friend. This activity makes use of the RecyclerAdapter and ChatMessage classes which are used to fetch and display messages in real-time.
