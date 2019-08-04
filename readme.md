# Bungee Web
A simple plugin that allows your players to access any web servers such as dynmap, that are running on any of the proxied servers.

Download: https://www.spigotmc.org/resources/bungee-web.70032/

Features:
 - Custom port
 - Automatic IP from server config option
 - CSS customisation

TODO:
 - Image forwarding


Config:
```YAML
port: 8080
debug: false
links:
   dynmap:
      name: Survival Dynmap
      server: survival
      port: 8123
   sg-stats:
      name: Survival Games Stats
      server: survivalgames
      port: 8080
```