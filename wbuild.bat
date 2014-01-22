for /D %%f in (obj\*) do rmdir %%f /s /q
for /D %%f in (libs\*) do rmdir %%f /s /q
ndk-build
