
# Docker


## Builds and runs a container

- builds Web-Snapshot from sources,
- builds a Docker image
- runs a container based the freshly built image

```bash
git clone https://github.com/Asqatasun/Web-Snapshot.git
cd Web-Snapshot
docker/compile_and_build_docker_image.sh -l -s ${PWD} -d docker/SNAPSHOT-local_from-Ubuntu
```


