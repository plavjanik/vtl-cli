import datetime
import logging
import os

from ptg2 import zos
from ptg2.zos import write_file
from ptg2.zos.unix import get_unix

log = logging.getLogger(__name__)


def upload_to_zos(local_path, zos_path):
    if zos.exists(zos_path):
        remote_stat = zos.stat(zos_path)
    else:
        remote_stat = None
    local_stat = os.stat(local_path)
    if not remote_stat or datetime.datetime.fromtimestamp(local_stat.st_mtime, remote_stat.st_mtime.tzinfo) > remote_stat.st_mtime:
        log.info(f"Copying file {local_path} to {zos_path}")
        with open(local_path, "rb") as f:
            zos.write_file(f, zos_path, mode="b")


def main():
    deploy_dir = "/tmp/vtl-cli"
    if not zos.exists(deploy_dir):
        zos.makedirs(deploy_dir)
    upload_to_zos("build/vtl-cli.jar", zos.unix_join(deploy_dir, "vtl-cli.jar"))
    upload_to_zos("build/zos/vtl", zos.unix_join(deploy_dir, "vtl"))
    with get_unix() as unix:
        unix.do(f'cd {deploy_dir}')
        unix.do('chmod a+x vtl')
        unix.do(f'echo "Hello, \$name!" > hello.vtl')
        unix.do(f'echo "name: world" > hello.yml')
        print(unix.do(f'java -jar vtl-cli.jar -y hello.yml -o hello.txt -ie Cp1047 -oe Cp1047 hello.vtl'))
        print('hello.vtl:', unix.do(f'cat hello.vtl'))
        print('hello.yml:', unix.do(f'cat hello.yml'))
        result = unix.do(f'cat hello.txt')
        print('hello.txt:', result)
        expected = "Hello, world!"
        if result != expected:
            log.error(f"'{result}' != '{expected}'")
        else:
            print("OK")


if __name__ == '__main__':
    main()
