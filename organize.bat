set SERVER="%USERPROFILE%/Dropbox/PC/Desktop/uni/Modules/3rdYear/LZSCC311-DistributedSystems/coursework/server"
set CLIENT="%USERPROFILE%/Dropbox/PC/Desktop/uni/Modules/3rdYear/LZSCC311-DistributedSystems/coursework/client"
set TWOTHIRD="0.6666666666666666667"

wt -M -d %SERVER% ; sp -H -d %CLIENT% -s 0.7 ; sp -VD -s %TWOTHIRD% ; sp -VD -s 0.5 ; mf up ; sp -VD -s %TWOTHIRD% ; sp -VD -s 0.5
