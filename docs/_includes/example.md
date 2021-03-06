# Example: {{ page.title }}
{: .no_toc }

{% include toc.md %}

{% if include.description %}
{{ include.description }}
{% endif %}

## docker-compose
Given the following docker-compose:
```yaml
{% include generated/{{ include.name }}.yaml %}
```

## Output (markdown) 

Using the default output, the following mermaid diagram is generated:

```
{% include generated/{{ include.name }}-default.txt %}
```

## Images (svg)

Using default theme (`--with-bg` option):

<img src="{{ site.baseurl }}/assets/generated/{{ include.name }}-default.svg" class="img-responsive">

Or dark theme (`--with-bg` option):

<img src="{{ site.baseurl }}/assets/generated/{{ include.name }}-dark.svg">
