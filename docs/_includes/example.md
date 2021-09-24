# Example: {{ page.title }}
{: .no_toc }

{% include toc.md %}

## docker-compose
Given the following docker-compose:
```yaml
{% include generated/{{ include.name }}.yaml %}
```

## Output (markdown) 

Using the default output, the following mermaid diagram is generated:

{% include generated/{{ include.name }}-default.md %}

## Images (svg)

Using default theme (background forced):

<image src="{{ site.baseurl }}/assets/generated/{{ include.name }}-default.svg" class="img-responsive" />

Or dark theme (background forced):
<image src="{{ site.baseurl }}/assets/generated/{{ include.name }}-dark.svg" />