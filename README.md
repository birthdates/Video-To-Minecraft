# Video to Minecraft ![Release Workflow](https://github.com/birthdates/Video-To-Minecraft/actions/workflows/maven.yml/badge.svg)

This plugin is meant to read video files and allow you to watch it in minecraft using ffmpeg *(no audio)*

This plugin targets the 1.7-1.17 Bukkit API

# Prerequisites

* FFMPEG installed to your path

# Commands

* `/watch <video>` - This command searches for a video in the plugin's data folder (`plugins/VideoToMinecraft`) and
  plays it on a map item *(requires `videotominecraft.watch` permission)*
* `/watchmovie <video>` - This command does the same as `/watch` however, it uses item frames and multiple maps to
  increase the resolution of your video *(requires `videotominecraft.watchmovie` permission)*
* `/stopwatching` - This command allows you to stop watching a video (only with `/watch`)

# Known Issues

* The max extraction time is 120 seconds which can limit your input video's length

# FPS
FPS is a limited factor due to ping. For example 20 FPS requires less than or equal to 50ms of ping. The equation for this is 1000 / FPS. 

# Next Steps

* Resource pack server to host a templated pack with audio

# Previews

## Maps

![](previews/map.gif)

## Movies

![](previews/movies.gif)