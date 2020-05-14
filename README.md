# Franco Toolkit System
(There is no other good name for this at the moment)

--
**WARNING** This project is in VERY EARLY development

FTS aims to fill the missing gap between developers and desktop applications.
Initially designed for Linux apps, it should easly support Mac and Windows apps as well.
Support for other systems like Raspberry Pi in Framebuffer mode is considered by design.

... and Android!

**Now into more technical details**

FTS is a Java based API to help developers write desktop applications with abstract
user interfaces. Initially any developer with some experience in Android should have
no problem creating FTS applications.

This work is a derivate of a small library that I wrote for [MusicTrans](http://musictransapp.com), and now
I'm extending it for more diverse applications.  

As a reference, it took me only one month to port the code of MusicTrans for Android to Mac/Win/Linux,
and most of the code is shared between these very different systems.

## Architecture

We can distingish three main blocks: Application code, FTS code and System level code. As you can see in the diagram, application code only talks to FTS and only if needed, to the native operating system. For example, Android apps may need to handle Android permissions at some point, and Linux app may need some files for packaging.

In the middle is the FTS code that you will find in this repository. The FTS core is fully platform/system agnostic, and that's what the apps should be built over. Below the core part is where the drivers for different rendering backend exist, currently there is only an OpenGL ES based driver, with he help of NanoVG as a drawing implementation.

At the bottom is the operating system and system libraries.

![FTS Architecture Diagram](http://franco.arealinux.cl/files/fts-architecture.png)

Current target is Linux / Android apps using OpenGL ES and NanoVG. While there was a SWT driver that was used to try the basic ideas at the beginning, that driver is not being developed anymore.

Franco Catrin
2016-2020
