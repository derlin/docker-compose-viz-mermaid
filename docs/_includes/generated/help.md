Usage: cli [OPTIONS] [docker-compose-path]

  Visualize a docker-compose, by converting it to a Mermaid graph.

  Supported outputs (`-f`):

  * 'text' (default) outputs the mermaid graph (use -o to output to a file instead of stdout);
  * 'markdown' is same as text, but wraps the graph text in '```mermaid```'
  * 'png' or 'svg' generates the image and saves it 'image.[png|svg]' (use -o to change the destination);
  * 'editor' // 'preview' generates a link to the mermaid online editor, and print it to the console.

  You can further customize the result using the options below.

Processing options:
  -p, --ports / -P, --no-ports      Extract and display ports
  -v, --volumes / -V, --no-volumes  Extract and display volumes
  -l, --ilinks / -L, --no-ilinks    Try to find implicit links between services by looking at the
                                    environment variables

Output options:
  -f, --format [TEXT|MARKDOWN|EDITOR|PREVIEW|PNG|SVG]
                                            Output type (case insensitive) (default: TEXT)
  -o, --out PATH                            Write output to a specific file
  -b, --with-bg                             Force background color
  -d, --dir [LR|RL|TB|BT]                   Graph orientation (default: TB)
  -t, --theme [DEFAULT|DARK]                Graph theme (default: DEFAULT)
  -c, --classes / -C, --no-classes          Add CSS classes to mermaid to make the output nicer

Options:
  --version   Show the version and exit
  -h, --help  Show this message and exit