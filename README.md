# ⚡️OreoCore (Abandoned)
The core api for my plugins. This is a really old code that I written when I had only 2 years experience on programming, so expect some really silly and un logical codes here. If you are a plugin developer that needs to edit my plugin or create addon I'd recommend to just re create the whole thing rather than editing this source, its much more worth your time.. trust me!

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

## Known database problem
If you happened to change your `online-mode` settings to true or false, you'll have to delete `playerdata.db` since player's UUID will change on that convertion.
