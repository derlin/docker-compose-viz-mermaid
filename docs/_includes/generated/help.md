Usage: cli [OPTIONS] [docker-compose-path]

  Generate a mermaid graph from a docker-compose file.

  There are different kind of outputs:

  * 'text' (default) outputs the mermaid graph (use -o to output to a file instead of stdout);
  * 'markdown' is same as text, but wraps the graph text in '```mermaid```'
  * 'png' or 'svg' generates the image and saves it 'image.[png|svg]' (use -o to change the destination);
  * 'editor' // 'preview' generates a link to the mermaid online editor, and print it to the console.

  When using theme and classes, the output may become hard to read depending on the background.
  It is thus possible to force a background (using a hack) with the option `-b`.

Options:
  -d, --dir [LR|RL|TB|BT]                   Graph orientation (default: TB)
  -t, --theme [DEFAULT|DARK]                Graph theme (default: DEFAULT)
  -p, --ports / -P, --no-ports
  -v, --volumes / -V, --no-volumes
  -l, --ilinks / -L, --no-ilinks            If set, try to find implicit links/depends_on by looking
                                            at the environment variables, see if one if pointing to
                                            the host:port of another service
  -c, --classes / -C, --no-classes          If set, add some classes to mermaid to make the output
                                            nicer
  -f, --format [TEXT|MARKDOWN|EDITOR|PREVIEW|PNG|SVG]
                                            Control the output format, case-insensitive. (default:
                                            TEXT)
  -b, --with-bg / -B, --no-bg               If set, try to find implicit links/depends_on by looking
                                            at the environment variables, see if one if pointing to
                                            the host:port of another service
  -o, --out PATH                            Only available for format TEXT and PNG
  -h, --help                                Show this message and exit