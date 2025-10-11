import { Input } from "@/components/ui/input";
import { Search } from "lucide-react";

interface TableSearchProps {
  handleSearch: (e: React.FormEvent) => void;
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  placeholder: string;
}

export const TableSearch = ({
  handleSearch,
  searchQuery,
  setSearchQuery,
  placeholder,
}: TableSearchProps) => {
  return (
    <form onSubmit={handleSearch} className="relative">
      <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
      <Input
        type="text"
        placeholder={placeholder}
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        className="pl-10 h-8 w-[300px]"
        onKeyDown={(e) => {
          if (e.key === "Enter") {
            e.preventDefault();
            handleSearch(e);
          }
        }}
      />
    </form>
  );
};
