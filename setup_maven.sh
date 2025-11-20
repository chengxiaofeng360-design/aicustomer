#!/bin/bash

set -euo pipefail

MAVEN_VERSION="3.9.6"
INSTALL_DIR="$HOME/opt/apache-maven-$MAVEN_VERSION"
ARCHIVE_URL="https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
ARCHIVE_PATH="$HOME/opt/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
SHELL_RC="$HOME/.zshrc"

mkdir -p "$HOME/opt"
touch "$SHELL_RC"

if [ ! -d "$INSTALL_DIR" ]; then
  echo "Downloading Apache Maven $MAVEN_VERSION..."
  curl -L "$ARCHIVE_URL" -o "$ARCHIVE_PATH"
  echo "Extracting Maven to $INSTALL_DIR..."
  tar -xzf "$ARCHIVE_PATH" -C "$HOME/opt"
  rm -f "$ARCHIVE_PATH"
fi

echo "Maven installed at: $INSTALL_DIR"

ENV_BLOCK=$(cat <<EOF

# Maven environment
export MAVEN_HOME="$INSTALL_DIR"
export PATH="\$MAVEN_HOME/bin:\$PATH"
EOF
)

if ! grep -q "MAVEN_HOME" "$SHELL_RC"; then
  echo "Updating $SHELL_RC to include Maven PATH..."
  printf "%s\n" "$ENV_BLOCK" >> "$SHELL_RC"
  echo "Run 'source $SHELL_RC' or open a new terminal to apply changes."
else
  echo "MAVEN_HOME already configured in $SHELL_RC."
fi

echo "Current Maven version:"
"$INSTALL_DIR/bin/mvn" -v || true

