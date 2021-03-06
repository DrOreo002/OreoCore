# ⚡️OreoCore
[![forthebadge](https://forthebadge.com/images/badges/works-on-my-machine.svg)](https://forthebadge.com) [![forthebadge](https://forthebadge.com/images/badges/built-with-swag.svg)](https://forthebadge.com)  
The core api for my plugins. This has the core features that I'll need to develop my plugins, basically removes the hassle of copy and pasting codes.

**Core features**
- Inventory API
- World/Player/Location/etc Utilities
- Material Utilities (Using XMaterial and XMaterial)
- Databases (Easy SQL/FlatFile/MySQL databse management)
- Object Oriented command API (Per class argument)
- Annotation driven configuration (Easy use of configuration a.k.a yaml file, no more spaghetti code!)
- Logging utilities
- Conversation utilities (Create conversation easily and get user's response directly!)

## 📽 Documentation
Documentation are not hosted online, but all the useful class is well documented already. Check it out!

## 🛠 Building
To build please use gradle. Commands are `clean build shadowJar`

## Known database problem
If you happened to change your `online-mode` settings to true or false, you'll have to delete `playerdata.db` since player's UUID will change on that convertion.
