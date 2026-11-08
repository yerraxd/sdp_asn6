# sdp_asn6: Visitor Pattern, Advanced File System

Assignment 06, Software Design Patterns. Java 17, Maven, JUnit 5.

## Commands

```bash
mvn test                      # run all tests
mvn -q compile exec:java      # run the deterministic demo (filesystem.App)
```

## Design

- `Element` / `Visitor`: the contracts. `Visitor` has `visit(FileNode)` and `visit(DirectoryNode)`.
- `Node`: shared name validation and parent link. `FileNode` (non-negative size) and `DirectoryNode` (children).
- Double dispatch: `accept(visitor)` in each element calls `visitor.visit(this)`. The first dispatch picks
  `accept` by the element's runtime type, the second picks the `visit` overload by the type of `this`.
  There is no `instanceof`, `getClass()`, reflection or cast anywhere.
- Traversal owner: `TraversingVisitor` (a visitor base class). It walks depth-first, children in name order,
  and visits every node once. Concrete visitors only implement `onFile` / `onDirectory`. Visitors own the
  traversal because they need context (depth, current path) that a plain element cannot give.
- Paths and depth: the base class keeps a stack of directory names and a depth counter. The root directory
  has depth 0 and is not part of the path. A file is one level below its directory.
- Search output: paths use `/`, are relative to the chosen root, and are sorted as whole strings at the end
  (per-level name order is not the same as whole-path order).
- State: visitors are reusable only through `runOn(root)`, which resets state first. Calling
  `root.accept(visitor)` directly does not reset, so it is not the supported entry point.
- Integrity: negative sizes, blank names or names with `/`, duplicate sibling names, a second parent and
  cycles are rejected with exceptions before anything changes. `children()` is an unmodifiable view.

## Extension boundary

`StatisticsVisitor` was added in its own commit without changing `Element`, `Node`, `FileNode`, `DirectoryNode`
or their `accept` methods. A new visitor is cheap. A new element type is expensive: `Visitor` needs a new
`visit` overload and every existing visitor must implement it.

## Tests

`DoubleDispatchTest`, `TreeIntegrityTest`, `TotalSizeVisitorTest`, `SearchVisitorTest`,
`StatisticsVisitorTest`, `VisitorStateTest` cover checkpoints T1 to T8.

## Sample output

`mvn -q compile exec:java` on the demo tree built in `App`:

```
Total size: 13900 bytes
Java files >= 1000 bytes:
  src/main/App.java
  src/test/AppTest.java
Files: 6, directories: 6, max depth: 3
```

Demo tree (sizes in bytes):

```
project
├── README.md (1200)
├── pom.xml (2100)
├── src
│   ├── main
│   │   ├── App.java (3400)
│   │   └── Util.java (900)
│   └── test
│       └── AppTest.java (1800)
├── docs
│   └── design.md (4500)
└── target (empty)
```

`Util.java` is excluded from the search because 900 < 1000. `target` is an empty directory:
it counts as a directory and adds no bytes. The deepest node is `App.java` at depth 3
(project = 0, src = 1, main = 2, App.java = 3).
