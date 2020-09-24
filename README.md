# Downloader
An application to let you download files from the internet.

# Basic features:
- [x] Allow users to download a very large file from an arbitrary URL.
- [x] Allow the download to run in the background while user is interacting with other apps.
- [x] Support resumable download.

# Advanced features:
- [x] Show progress on a notification and handle interactions with the download (pause, resume, stop,...). 
- [x] Show download speed.
- [x] Show remaining time estimation.
- [x] Delete a download request by long press on the download item in the list.
- [ ] Allow user to select download destination (instead of the default Download folder).

# Robustness:
- [x] Automatically resume downloads when network is available again.
- [x] Automatically resume downloads when the phone has just rebooted and the app is just opened.
- [x] Handle edge cases: invalid URL, file size is unknown, server doesn't support HTTP range requests,…
- [x] Support redirects: if the URL is not a direct link but requires redirection, the app should be able to handle that.
- [x] Handle super edge case, for example: user starts a download > user pauses the download > user disabled external storage permission > user resumes the download > the app automatically resume the download in the background and try to write into external storage > exception because don’t have permission.
- [ ] NEED TESTING: Handle different battery saving modes (Doze Mode, App Standby, Loosing Wake Lock,…).
- [ ] NEED TESTING: Handle the case where the file on server has changed while the app is downloading the file (based on "Last Modified”).

# Performance:
- [x] Having a download queue which supports request prioritization: we might want to download the small files first so user has access to those files instantly, even if those small files are added into the download queue after other files. 
- [x] Download a file in multiple chunks and combine all the chunks together when done. 
- [ ] Auto increase/decrease the number of download requests running at a given moment based on network speed.
- [ ] Support different retry mechanism if needed.
