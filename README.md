# chatting-app
CS 656 Programming Project

# MainActivity
Loads the home_screen.xml layout to display the user's current friends. If no user is signed in, they will be prompted for an email address. If the email is registered under an existing user, then they will be prompted for a password. Otherwise, the user will be prompted to create an account. 
## Adding friends
Under the three dots, clicking on "Add friends" will load the FindFriendsActivity which allows the user to search for and add friends.
## Sending messages
Tapping on a friend under the main layout (Should say "Messages" on the top ofthe screen) will create the MessageScreenActivity.

# FindFriendsActivity
Loads the find_friend_screen.xml layout to provide users a way to search for friends to add. Try searching for "Test User" and add one of them. They will appear when you go back to the main layout.

# MessageScreenActivity
Loads the messages_screen.xml layout to display the conversation between the user and the selected friend. This activity makes use of the RecyclerAdapter and ChatMessage classes which are used to fetch and display messages in real-time.
