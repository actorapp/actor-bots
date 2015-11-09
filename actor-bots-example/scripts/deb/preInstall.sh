export USER=actor
export GROUP=actor

# check that owner group exists
if [ -z "$(getent group ${GROUP})" ]; then
  groupadd ${GROUP}
fi

# check that user exists
if [ -z "$(getent passwd ${USER})" ]; then
  useradd --gid ${GROUP} ${USER}
fi

# (optional) check that user belongs to group
if ! id -G -n ${USER} | grep -qF ${GROUP} ; then
  usermod -a -G ${GROUP} ${USER}
fi
