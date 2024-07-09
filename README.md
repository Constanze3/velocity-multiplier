# Velocitymultiplier
A simple Minecraft mod to make things go faster/slower.

## Dependencies
```
"fabricloader": ">=0.15.11",
"minecraft": "~1.21",
"java": ">=21",
"fabric-api": "*"
```

## Usage 
To set the default multipler.
```
/velocitymultiplier default <multiplier>
```

To set an "override" multiplier for certain entities.
To reset the "override" multiplier to default simply set it to the same value as the default multiplier.
```
/velocitymultiplier <targets> <multiplier>
```

The multiplier is a vector consisting of 3 double values.
```
<multiplier> = <x> <y> <z>
```
